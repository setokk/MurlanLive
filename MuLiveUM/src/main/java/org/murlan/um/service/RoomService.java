package org.murlan.um.service;

import org.murlan.um.api.dto.RoomDto;
import org.murlan.um.model.GameStateEntity;
import org.murlan.um.model.RoomEntity;
import org.murlan.um.model.ScoreEntity;
import org.murlan.um.model.ScoreTotalEntity;
import org.murlan.um.repository.RoomRepository;
import org.murlan.um.repository.ScoreTotalRepository;
import org.murlan.um.service.mapper.GameStateMapper;
import org.murlan.um.service.mapper.RoomMapper;
import org.murlan.um.service.mapper.ScoreMapper;
import org.murlan.um.service.mapper.ScoreTotalMapper;
import org.murlan.um.service.param.room.CreateRoomParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoomService {
    private final RoomRepository roomRepository;
    private final ScoreTotalRepository scoreTotalRepository;
    private final RoomMapper roomMapper;
    private final GameStateMapper gameStateMapper;
    private final ScoreTotalMapper scoreTotalMapper;

    @Autowired
    public RoomService(
            RoomRepository roomRepository,
            ScoreTotalRepository scoreTotalRepository,
            RoomMapper roomMapper,
            GameStateMapper gameStateMapper,
            ScoreTotalMapper scoreTotalMapper
    ) {
        this.roomRepository = roomRepository;
        this.scoreTotalRepository = scoreTotalRepository;
        this.roomMapper = roomMapper;
        this.gameStateMapper = gameStateMapper;
        this.scoreTotalMapper = scoreTotalMapper;
    }

    public RoomDto createRoom(CreateRoomParam param) {
        List<GameStateEntity> gameStates = param.gameStates().stream()
                .map(gameStateMapper::toEntity)
                .toList();

        RoomEntity room = roomMapper.toEntity(param, gameStates);
        gameStates.forEach(gs -> gs.setRoom(room));
        RoomEntity savedRoom = roomRepository.save(room);

        List<ScoreTotalEntity> totalScores = param.totalScores().entrySet().stream()
                .map(totalScore -> {
                    boolean isWinner = totalScore.getValue() >= savedRoom.getTotalScoreToWin();
                    return scoreTotalMapper.toEntity(totalScore, savedRoom.getId(), isWinner);
                })
                .toList();
        scoreTotalRepository.saveAll(totalScores);

        return roomMapper.toDto(savedRoom);
    }
}
