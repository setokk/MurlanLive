package org.murlan.um.service;

import org.murlan.um.error.BusinessLogicException;
import org.murlan.um.model.PlayerEntity;
import org.murlan.um.model.dto.PlayerDto;
import org.murlan.um.repository.PlayerRepository;
import org.murlan.um.service.param.LoginPlayerParam;
import org.murlan.um.service.param.RegisterPlayerParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Service
public class PlayerService {
    private final PlayerRepository playerRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthService authService;

    @Autowired
    public PlayerService(PlayerRepository playerRepository, PasswordEncoder passwordEncoder, AuthService authService) {
        this.playerRepository = playerRepository;
        this.passwordEncoder = passwordEncoder;
        this.authService = authService;
    }

    public PlayerDto loginPlayer(LoginPlayerParam param) {
        PlayerEntity player = playerRepository
                .findPlayerByUsername(param.username())
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
        PlayerEntity savedPlayer = playerRepository.save(new PlayerEntity(param.username(), passwordEncoder.encode(param.password()), LocalDateTime.now()));
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
