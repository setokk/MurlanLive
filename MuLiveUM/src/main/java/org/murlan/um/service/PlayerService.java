package org.murlan.um.service;

import org.murlan.um.error.BusinessLogicException;
import org.murlan.um.model.PlayerEntity;
import org.murlan.um.model.dto.PlayerDto;
import org.murlan.um.repository.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Service
public class PlayerService {
    private final PlayerRepository playerRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public PlayerService(PlayerRepository playerRepository, PasswordEncoder passwordEncoder) {
        this.playerRepository = playerRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public PlayerDto loginPlayer(String username, String password) {
        PlayerEntity player = playerRepository
                .findPlayerByUsername(username)
                .orElseThrow(() -> new BusinessLogicException(HttpStatus.NOT_FOUND, "Invalid credentials"));

        String actualHashedPassword = player.getPassword();
        boolean isValidCredentials = passwordEncoder.matches(password, actualHashedPassword);
        if (!isValidCredentials) {
            throw new BusinessLogicException(HttpStatus.NOT_FOUND, "Invalid credentials");
        }
        return new PlayerDto(player.getId(), player.getUsername(), player.getCreatedDate().atZone(ZoneId.systemDefault()));
    }

    public PlayerDto registerPlayer(String username, String password) {
        boolean usernameExists = playerRepository.findPlayerByUsername(username).isPresent();
        if (usernameExists) {
            throw new BusinessLogicException(HttpStatus.CONFLICT, "Player with username: " + username + " exists");
        }
        PlayerEntity savedPlayer = playerRepository.save(new PlayerEntity(username, passwordEncoder.encode(password), LocalDateTime.now()));
        return new PlayerDto(savedPlayer.getId(), savedPlayer.getUsername(), savedPlayer.getCreatedDate().atZone(ZoneId.systemDefault()));
    }
}
