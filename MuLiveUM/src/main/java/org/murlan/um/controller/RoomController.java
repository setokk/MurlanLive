package org.murlan.um.controller;

import jakarta.validation.Valid;
import org.murlan.um.api.CreateRoomRequest;
import org.murlan.um.service.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/room")
public class RoomController {
    private final RoomService roomService;

    @Autowired
    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    @PutMapping("/create")
    public ResponseEntity<?> createRoom(@RequestBody @Valid CreateRoomRequest request) {
        request.validate();
        roomService.
    }

}

