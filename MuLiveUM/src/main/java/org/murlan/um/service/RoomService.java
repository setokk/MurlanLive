package org.murlan.um.service;

import org.murlan.um.api.dto.RoomDto;
import org.murlan.um.repository.RoomRepository;
import org.murlan.um.service.param.room.CreateRoomParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RoomService {
    private final RoomRepository roomRepository;

    @Autowired
    public RoomService(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    public RoomDto createRoom(CreateRoomParam param) {

    }
}
