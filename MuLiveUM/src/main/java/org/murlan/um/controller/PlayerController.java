package org.murlan.um.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("/api/players")
public class PlayerController {
    @PostMapping("/login")
    public ResponseEntity<?> loginPlayer() {
        return ResponseEntity.ok().build();
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerPlayer() {
        return ResponseEntity.ok().build();
    }
}
