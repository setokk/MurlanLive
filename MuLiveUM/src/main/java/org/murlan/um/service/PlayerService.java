package org.murlan.um.service;

import jakarta.mail.MessagingException;
import org.murlan.um.error.BusinessLogicException;
import org.murlan.um.model.PlayerEntity;
import org.murlan.um.model.PlayerResetPasswordEntity;
import org.murlan.um.model.dto.PlayerDto;
import org.murlan.um.repository.PlayerRepository;
import org.murlan.um.repository.PlayerResetPasswordEntityRepository;
import org.murlan.um.security.TokenGenerator;
import org.murlan.um.service.param.LoginPlayerParam;
import org.murlan.um.service.param.RegisterPlayerParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class PlayerService {
    private final PlayerRepository playerRepository;
    private final PlayerResetPasswordEntityRepository resetPasswordRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthService authService;
    private final TokenGenerator tokenGenerator;
    private final EmailTemplateService emailTemplateService;
    private final EmailService emailService;

    @Autowired
    public PlayerService(
            PlayerRepository playerRepository,
            PlayerResetPasswordEntityRepository resetPasswordRepository,
            PasswordEncoder passwordEncoder,
            AuthService authService,
            TokenGenerator tokenGenerator,
            EmailTemplateService emailTemplateService,
            EmailService emailService
    ) {
        this.playerRepository = playerRepository;
        this.resetPasswordRepository = resetPasswordRepository;
        this.passwordEncoder = passwordEncoder;
        this.authService = authService;
        this.tokenGenerator = tokenGenerator;
        this.emailTemplateService = emailTemplateService;
        this.emailService = emailService;
    }

    public PlayerDto loginPlayer(LoginPlayerParam param) {
        PlayerEntity player = playerRepository
                .findPlayerByUsernameOrEmail(param.usernameOrEmail())
                .orElseThrow(() -> new BusinessLogicException(HttpStatus.NOT_FOUND, "Invalid credentials"));

        String actualHashedPassword = player.getPassword();
        boolean isValidCredentials = passwordEncoder.matches(param.password(), actualHashedPassword);
        if (!isValidCredentials) {
            throw new BusinessLogicException(HttpStatus.NOT_FOUND, "Invalid credentials");
        }
        return new PlayerDto(player.getId(), player.getUsername(), player.getCreatedDate());
    }

    public PlayerDto registerPlayer(RegisterPlayerParam param) {
        boolean usernameExists = playerRepository.findPlayerByUsername(param.username()).isPresent();
        if (usernameExists) {
            throw new BusinessLogicException(HttpStatus.CONFLICT, "Player with username: " + param.username() + " exists");
        }

        boolean emailExists = param.email() != null && playerRepository.findPlayerByEmail(param.email()).isPresent();
        if (emailExists) {
            throw new BusinessLogicException(HttpStatus.CONFLICT, "Player with email: " + param.email() + " exists");
        }

        PlayerEntity savedPlayer = playerRepository.save(new PlayerEntity(param.username(), passwordEncoder.encode(param.password()), param.email(), LocalDateTime.now()));
        return new PlayerDto(savedPlayer.getId(), savedPlayer.getUsername(), savedPlayer.getCreatedDate());
    }

    public PlayerDto getPlayer(long playerId) {
        PlayerEntity player = playerRepository.findById(playerId)
                .orElseThrow(() -> new BusinessLogicException(HttpStatus.NOT_FOUND, "Player with id: " + playerId + " not found"));
        return new PlayerDto(player.getId(), player.getUsername(), player.getCreatedDate());
    }

    @Transactional
    public PlayerDto blockPlayer(long playerToBlockId) {
        PlayerBlockContext context = getPlayerBlockContext(playerToBlockId);

        if (!context.blockedPlayers().contains(context.playerToBlock())) {
            context.blockedPlayers().add(context.playerToBlock());
            playerRepository.save(context.player());
        }

        return new PlayerDto(context.playerToBlock().getId(), context.playerToBlock().getUsername(), context.playerToBlock().getCreatedDate());
    }

    @Transactional
    public PlayerDto unblockPlayer(long playerToBlockId) {
        PlayerBlockContext context = getPlayerBlockContext(playerToBlockId);

        if (context.blockedPlayers().contains(context.playerToBlock())) {
            context.blockedPlayers().remove(context.playerToBlock());
            playerRepository.save(context.player());
        }

        return new PlayerDto(context.playerToBlock().getId(), context.playerToBlock().getUsername(), context.playerToBlock().getCreatedDate());
    }

    public List<PlayerDto> getBlockedPlayers() {
        PlayerDto playerDto = authService.getAuthenticatedUser();
        PlayerEntity player = playerRepository.findById(playerDto.getId())
                .orElseThrow(() -> new BusinessLogicException(HttpStatus.NOT_FOUND, "Requesting player with id: " + playerDto.getId() + " not found"));

        return player.getBlockedPlayers().stream()
                .map(p -> new PlayerDto(p.getId(), p.getUsername(), p.getCreatedDate()))
                .toList();
    }

    @Transactional
    public void forgotPassword(String usernameOrEmail) {
        PlayerEntity player = playerRepository.findPlayerByUsernameOrEmail(usernameOrEmail)
                .orElseThrow(() -> new BusinessLogicException(HttpStatus.NOT_FOUND, "Username or email not found"));

        if (player.getEmail() == null) {
            throw new BusinessLogicException(HttpStatus.NOT_FOUND, "User has no email assigned");
        }

        PlayerResetPasswordEntity resetPassword = new PlayerResetPasswordEntity(
                null,
                tokenGenerator.generateToken(),
                LocalDateTime.now().plusHours(12),
                player
        );
        PlayerResetPasswordEntity savedResetPassword = resetPasswordRepository.save(resetPassword);

        try {
            String resetPasswordLink = ServletUriComponentsBuilder
                    .fromCurrentContextPath()
                    .path("/reset-password.html")
                    .queryParam("token", savedResetPassword.getToken())
                    .build()
                    .toUriString();

            String renderedHtml = emailTemplateService.render(
                    EmailTemplateService.FORGOT_PASSWORD, Map.of(
                            "username", player.getUsername(),
                            "email", player.getEmail(),
                            "reset-password-link", resetPasswordLink
                    ));

            emailService.sendMail(
                    player.getEmail(),
                    "MuLive: Request for Password Reset",
                    renderedHtml,
                    (helper) -> helper.addInline(
                            "logo",
                            new ClassPathResource("static/images/logo.png"),
                            "image/png"
                    )
            );
        } catch (MessagingException e) {
            throw new BusinessLogicException(HttpStatus.INTERNAL_SERVER_ERROR, "There was an error with the email service. Please try again later");
        }
    }

    @Transactional
    public void resetPassword(String token, String newPassword) {
        PlayerResetPasswordEntity resetPassword = resetPasswordRepository.findByToken(token)
                .orElseThrow(() -> new BusinessLogicException(HttpStatus.NOT_FOUND, "Reset password token not found"));

        if (LocalDateTime.now().isAfter(resetPassword.getExpiresAt())) {
            return;
        }

        PlayerEntity player = resetPassword.getPlayer();
        player.setPassword(passwordEncoder.encode(newPassword));
        playerRepository.save(player);

        resetPasswordRepository.delete(resetPassword);
    }

    private PlayerBlockContext getPlayerBlockContext(long playerToBlockId) {
        PlayerDto playerDto = authService.getAuthenticatedUser();
        PlayerEntity player = playerRepository.findById(playerDto.getId())
                .orElseThrow(() -> new BusinessLogicException(HttpStatus.NOT_FOUND, "Requesting player with id: " + playerDto.getId() + " not found"));

        if (playerToBlockId == playerDto.getId()) {
            throw new BusinessLogicException(HttpStatus.BAD_REQUEST, "The player to be blocked cannot be the same as the one doing the request");
        }

        PlayerEntity playerToBlock = playerRepository.findById(playerToBlockId)
                .orElseThrow(() -> new BusinessLogicException(HttpStatus.NOT_FOUND, "Player to be blocked with id: " + playerToBlockId + " not found"));

        return new PlayerBlockContext(player, playerToBlock, player.getBlockedPlayers());
    }

    private record PlayerBlockContext(
            PlayerEntity player,
            PlayerEntity playerToBlock,
            Set<PlayerEntity> blockedPlayers
    ) {}
}
