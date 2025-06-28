package org.murlan.um.controller;

import jakarta.validation.Valid;
import org.murlan.um.api.LoginPlayerRequest;
import org.murlan.um.api.RegisterPlayerRequest;
import org.murlan.um.auth.JwtUtils;
import org.murlan.um.model.dto.PlayerDto;
import org.murlan.um.service.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController("/api/players")
public class PlayerController {
    private final PlayerService playerService;

    @Autowired
    public PlayerController(PlayerService playerService) {
        this.playerService = playerService;
    }

    @PostMapping("/login")
    public ResponseEntity<String> loginPlayer(@RequestBody @Valid LoginPlayerRequest request) {
        request.validate();
        PlayerDto playerDto = playerService.loginPlayer(request.getUsername(), request.getPassword());
        return ResponseEntity.ok(JwtUtils.generateJWT(playerDto));
    }

    @PostMapping("/register")
    public ResponseEntity<String> registerPlayer(@RequestBody @Valid RegisterPlayerRequest request) {
        request.validate();
        PlayerDto playerDto = playerService.registerPlayer(request.getUsername(), request.getPassword());
        return ResponseEntity.ok(JwtUtils.generateJWT(playerDto));
    }

    @GetMapping("/validateJwt")
    public ResponseEntity<?> validateJwt() {
        return ResponseEntity.ok().build();
    }
}
