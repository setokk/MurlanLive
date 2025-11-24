package org.murlan.um.service;

import org.murlan.um.error.BusinessLogicException;
import org.murlan.um.model.PlayerEntity;
import org.murlan.um.api.dto.PlayerDto;
import org.murlan.um.repository.PlayerRepository;
import org.murlan.um.service.param.player.LoginPlayerParam;
import org.murlan.um.service.param.player.RegisterPlayerParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class PlayerService {
    private final PlayerRepository playerRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public PlayerService(PlayerRepository playerRepository, PasswordEncoder passwordEncoder) {
        this.playerRepository = playerRepository;
        this.passwordEncoder = passwordEncoder;
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
}
