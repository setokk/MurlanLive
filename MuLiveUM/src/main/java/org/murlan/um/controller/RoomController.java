package org.murlan.um.controller;

import jakarta.validation.Valid;
import org.murlan.um.api.dto.RoomDto;
import org.murlan.um.api.request.CreateRoomRequest;
import org.murlan.um.error.BusinessLogicException;
import org.murlan.um.service.RoomService;
import org.murlan.um.service.mapper.RoomMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/rooms")
public class RoomController {
    private final RoomService roomService;
    private final RoomMapper roomMapper;

    @Autowired
    public RoomController(RoomService roomService, RoomMapper roomMapper) {
        this.roomService = roomService;
        this.roomMapper = roomMapper;
    }

    @PostMapping("/create")
    public ResponseEntity<RoomDto> createRoom(@RequestBody @Valid CreateRoomRequest request) {
        RoomDto roomDto = roomService.createRoom(roomMapper.toParam(request));
        return ResponseEntity.ok(roomDto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getRoom(@PathVariable(name = "id") String roomId) {
        return ResponseEntity.ok("RoomDetails: " + roomId);
    }

    @GetMapping
    public ResponseEntity<List<RoomDto>> getRooms(
            @RequestParam(name = "pageNumber") int pageNumber,
            @RequestParam(name = "playerId") long playerId
    ) {
        if (pageNumber < 0) {
            throw new BusinessLogicException(HttpStatus.BAD_REQUEST, "pageNumber query parameter cannot be < 0");
        }
        if (playerId < 0) {
            throw new BusinessLogicException(HttpStatus.BAD_REQUEST, "playerId query parameter cannot be < 0");
        }
        List<RoomDto> rooms = roomService.getRooms(pageNumber, playerId);
        return ResponseEntity.ok(rooms);
    }
}
