package org.murlan.um.controller;

import jakarta.validation.Valid;
import org.murlan.um.api.request.LoginPlayerRequest;
import org.murlan.um.api.request.RegisterPlayerRequest;
import org.murlan.um.auth.JwtUtils;
import org.murlan.um.api.dto.PlayerDto;
import org.murlan.um.service.PlayerService;
import org.murlan.um.service.mapper.PlayerMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/players")
public class PlayerController {
    private final PlayerService playerService;
    private final PlayerMapper mapper;

    @Autowired
    public PlayerController(PlayerService playerService, PlayerMapper mapper) {
        this.playerService = playerService;
        this.mapper = mapper;
    }

    @PostMapping("/login")
    public ResponseEntity<String> loginPlayer(@RequestBody @Valid LoginPlayerRequest request) {
        PlayerDto playerDto = playerService.loginPlayer(mapper.toParam(request));
        return ResponseEntity.ok(JwtUtils.generateJWT(playerDto));
    }

    @PostMapping("/register")
    public ResponseEntity<String> registerPlayer(@RequestBody @Valid RegisterPlayerRequest request) {
        PlayerDto playerDto = playerService.registerPlayer(mapper.toParam(request));
        return ResponseEntity.ok(JwtUtils.generateJWT(playerDto));
    }

    @GetMapping("/validateJwt")
    public ResponseEntity<?> validateJwt() {
        return ResponseEntity.ok().build();
    }
}
