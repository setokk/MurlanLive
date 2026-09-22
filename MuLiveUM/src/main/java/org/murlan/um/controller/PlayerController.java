package org.murlan.um.controller;

import jakarta.validation.Valid;
import org.murlan.um.api.request.BlockPlayerRequest;
import org.murlan.um.api.request.ForgotPasswordRequest;
import org.murlan.um.api.request.LoginPlayerRequest;
import org.murlan.um.api.request.RegisterPlayerRequest;
import org.murlan.um.api.request.ResetPasswordRequest;
import org.murlan.um.api.request.UnblockPlayerRequest;
import org.murlan.um.auth.JwtUtils;
import org.murlan.um.model.dto.PlayerDto;
import org.murlan.um.service.PlayerService;
import org.murlan.um.service.mapper.PlayerMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/players")
public class PlayerController {
    private final PlayerService playerService;
    private final PlayerMapper playerMapper;

    @Autowired
    public PlayerController(PlayerService playerService, PlayerMapper playerMapper) {
        this.playerService = playerService;
        this.playerMapper = playerMapper;
    }

    @PostMapping("/login")
    public ResponseEntity<String> loginPlayer(@RequestBody @Valid LoginPlayerRequest request) {
        PlayerDto playerDto = playerService.loginPlayer(playerMapper.toParam(request));
        return ResponseEntity.ok(JwtUtils.generateJWT(playerDto));
    }

    @PostMapping("/register")
    public ResponseEntity<String> registerPlayer(@RequestBody @Valid RegisterPlayerRequest request) {
        PlayerDto playerDto = playerService.registerPlayer(playerMapper.toParam(request));
        return ResponseEntity.ok(JwtUtils.generateJWT(playerDto));
    }

    @GetMapping("/validateJwt")
    public ResponseEntity<?> validateJwt() {
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<PlayerDto> getPlayer(@PathVariable(name = "id") long playerId) {
        PlayerDto playerDto = playerService.getPlayer(playerId);
        return ResponseEntity.ok(playerDto);
    }

    @PostMapping("/block-player")
    public ResponseEntity<PlayerDto> blockPlayer(@RequestBody @Valid BlockPlayerRequest request) {
        PlayerDto blockedPlayerDto = playerService.blockPlayer(request.getPlayerToBlockId());
        return ResponseEntity.ok(blockedPlayerDto);
    }

    @PostMapping("/unblock-player")
    public ResponseEntity<PlayerDto> unblockPlayer(@RequestBody @Valid UnblockPlayerRequest request) {
        PlayerDto blockedPlayerDto = playerService.unblockPlayer(request.getPlayerToUnblockId());
        return ResponseEntity.ok(blockedPlayerDto);
    }

    @GetMapping("/get-blocked-players")
    public ResponseEntity<List<PlayerDto>> getBlockedPlayers() {
        List<PlayerDto> blockedPlayersDto = playerService.getBlockedPlayers();
        return ResponseEntity.ok(blockedPlayersDto);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody @Valid ForgotPasswordRequest request) {
        playerService.forgotPassword(request.getUsernameOrEmail());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody @Valid ResetPasswordRequest request) {
        playerService.resetPassword(request.getToken(), request.getNewPassword());
        return ResponseEntity.ok().build();
    }
}
