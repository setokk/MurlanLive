package org.murlan.um.controller;


import jakarta.validation.Valid;
import org.murlan.um.api.dto.RoomDto;
import org.murlan.um.api.request.CreateRoomRequest;
import org.murlan.um.service.RoomService;
import org.murlan.um.service.mapper.RoomMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/rooms")
public class RoomController {
    private final RoomService roomService;
    private final RoomMapper mapper;

    @Autowired
    public RoomController(RoomService roomService, RoomMapper mapper) {
        this.roomService = roomService;
        this.mapper = mapper;
    }

    @PostMapping("/create")
    public ResponseEntity<?> createRoom(@RequestBody @Valid CreateRoomRequest request) {
        RoomDto roomDto = roomService.createRoom(mapper.toParam(request));
        return ResponseEntity.ok(roomDto);
    }
}
