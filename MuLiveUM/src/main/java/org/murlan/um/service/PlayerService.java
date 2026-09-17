package org.murlan.um.service;

import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.murlan.um.error.BusinessLogicException;
import org.murlan.um.model.PlayerEmailVerificationEntity;
import org.murlan.um.model.PlayerEntity;
import org.murlan.um.model.PlayerResetPasswordEntity;
import org.murlan.um.model.dto.PlayerDetailsDto;
import org.murlan.um.model.dto.PlayerDto;
import org.murlan.um.repository.PlayerEmailVerificationRepository;
import org.murlan.um.repository.PlayerRepository;
import org.murlan.um.repository.PlayerResetPasswordEntityRepository;
import org.murlan.um.security.TokenGenerator;
import org.murlan.um.service.param.LoginPlayerParam;
import org.murlan.um.service.param.RegisterPlayerParam;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
@RequiredArgsConstructor
public class PlayerService {
    @Value("${mulive.email-verification-required}")
    private boolean isEmailVerificationRequired;

    private final PlayerRepository playerRepository;
    private final PlayerResetPasswordEntityRepository resetPasswordRepository;
    private final PlayerEmailVerificationRepository emailVerificationRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthService authService;
    private final TokenGenerator tokenGenerator;
    private final EmailTemplateService emailTemplateService;
    private final EmailService emailService;

    private static final ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();

    @Transactional
    public PlayerDto loginPlayer(LoginPlayerParam param) {
        PlayerEntity player = playerRepository
                .findPlayerByUsernameOrEmail(param.usernameOrEmail())
                .orElseThrow(() -> new BusinessLogicException(HttpStatus.NOT_FOUND, "Invalid credentials"));

        String actualHashedPassword = player.getPassword();
        boolean isValidCredentials = passwordEncoder.matches(param.password(), actualHashedPassword);
        if (!isValidCredentials) {
            throw new BusinessLogicException(HttpStatus.NOT_FOUND, "Invalid credentials");
        }

        if (isEmailVerificationRequired && !player.isVerified()) {
            throw new BusinessLogicException(HttpStatus.FORBIDDEN, "Player is not verified");
        }

        return new PlayerDto(player.getId(), player.getUsername(), player.getCreatedDate(), player.getEmail());
    }

    @Transactional
    public Optional<PlayerDto> registerPlayer(RegisterPlayerParam param) {
        boolean usernameExists = playerRepository.findPlayerByUsername(param.username()).isPresent();
        if (usernameExists) {
            throw new BusinessLogicException(HttpStatus.CONFLICT, "Player with username: " + param.username() + " exists");
        }

        boolean emailExists = param.email() != null && playerRepository.findPlayerByEmail(param.email()).isPresent();
        if (emailExists) {
            throw new BusinessLogicException(HttpStatus.CONFLICT, "Player with email: " + param.email() + " exists");
        }

        PlayerEntity savedPlayer = playerRepository.save(new PlayerEntity(param.username(), passwordEncoder.encode(param.password()), param.email(), LocalDateTime.now()));
        if (isEmailVerificationRequired) {
            PlayerEmailVerificationEntity emailVerification = new PlayerEmailVerificationEntity(null, tokenGenerator.generateToken(), savedPlayer);
            emailVerificationRepository.save(emailVerification);

            String verifyEmailLink = ServletUriComponentsBuilder
                    .fromCurrentContextPath()
                    .path("/verify-email.html")
                    .queryParam("token", emailVerification.getToken())
                    .build()
                    .toUriString();

            sendAsyncEmailVerificationEmail(verifyEmailLink, savedPlayer);
            return Optional.empty();
        } else {
            savedPlayer.setVerified(true);
        }

        return Optional.of(
                new PlayerDto(savedPlayer.getId(), savedPlayer.getUsername(), savedPlayer.getCreatedDate(), savedPlayer.getEmail())
        );
    }

    public PlayerDto getPlayer(long playerId) {
        PlayerEntity player = playerRepository.findById(playerId)
                .orElseThrow(() -> new BusinessLogicException(HttpStatus.NOT_FOUND, "Player with id: " + playerId + " not found"));

        String email = authService.getAuthenticatedUser().getId().equals(playerId)
                ? player.getEmail()
                : null;
        return new PlayerDto(player.getId(), player.getUsername(), player.getCreatedDate(), email);
    }

    public PlayerDetailsDto getPlayerDetails() {
        PlayerDto playerDto = authService.getAuthenticatedUser();
        return new PlayerDetailsDto();
    }

    @Transactional
    public PlayerDto blockPlayer(long playerToBlockId) {
        PlayerBlockContext context = getPlayerBlockContext(playerToBlockId);

        if (!context.blockedPlayers().contains(context.playerToBlock())) {
            context.blockedPlayers().add(context.playerToBlock());
            playerRepository.save(context.player());
        }

        return new PlayerDto(context.playerToBlock().getId(), context.playerToBlock().getUsername(), context.playerToBlock().getCreatedDate(), null);
    }

    @Transactional
    public PlayerDto unblockPlayer(long playerToBlockId) {
        PlayerBlockContext context = getPlayerBlockContext(playerToBlockId);

        if (context.blockedPlayers().contains(context.playerToBlock())) {
            context.blockedPlayers().remove(context.playerToBlock());
            playerRepository.save(context.player());
        }

        return new PlayerDto(context.playerToBlock().getId(), context.playerToBlock().getUsername(), context.playerToBlock().getCreatedDate(), null);
    }

    public List<PlayerDto> getBlockedPlayers() {
        PlayerDto playerDto = authService.getAuthenticatedUser();
        PlayerEntity player = playerRepository.findById(playerDto.getId())
                .orElseThrow(() -> new BusinessLogicException(HttpStatus.NOT_FOUND, "Requesting player with id: " + playerDto.getId() + " not found"));

        return player.getBlockedPlayers().stream()
                .map(p -> new PlayerDto(p.getId(), p.getUsername(), p.getCreatedDate(), null))
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

    @Transactional
    public void verifyEmail(String token) {
        PlayerEmailVerificationEntity emailVerification = emailVerificationRepository.findByToken(token)
                .orElseThrow(() -> new BusinessLogicException(HttpStatus.NOT_FOUND, "Email verification token not found"));

        PlayerEntity player = emailVerification.getPlayer();
        player.setVerified(true);

        emailVerificationRepository.delete(emailVerification);
    }

    private void sendAsyncEmailVerificationEmail(String verifyEmailLink, PlayerEntity player) {
        executor.execute(() -> {
            try {
                String renderedHtml = emailTemplateService.render(
                        EmailTemplateService.EMAIL_VERIFICATION, Map.of(
                                "username", player.getUsername(),
                                "email", player.getEmail(),
                                "verify-email-link", verifyEmailLink
                        ));

                emailService.sendMail(
                        player.getEmail(),
                        "MuLive: Verify your Email",
                        renderedHtml,
                        (helper) -> helper.addInline(
                                "logo",
                                new ClassPathResource("static/images/logo.png"),
                                "image/png"
                        )
                );
            } catch (MessagingException e) {
                e.printStackTrace();
            }
        });
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
