package org.murlan.um.service.mapper;

import org.murlan.um.api.dto.PlayerDto;
import org.murlan.um.api.dto.RoomDetailsDto;
import org.murlan.um.api.dto.RoomDto;
import org.murlan.um.api.request.CreateRoomRequest;
import org.murlan.um.model.GameStateEntity;
import org.murlan.um.model.PlayerEntity;
import org.murlan.um.model.RoomEntity;
import org.murlan.um.service.param.room.CreateRoomParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public final class RoomMapper {
    private final GameStateMapper gameStateMapper;
    private final ScoreTotalMapper scoreTotalMapper;

    @Autowired
    public RoomMapper(GameStateMapper gameStateMapper, ScoreTotalMapper scoreTotalMapper) {
        this.gameStateMapper = gameStateMapper;
        this.scoreTotalMapper = scoreTotalMapper;
    }

    public CreateRoomParam toParam(CreateRoomRequest request) {
        Map<PlayerDto, Short> totalScores = request.getTotalScores().entrySet()
                .stream()
                .collect(Collectors.toMap(
                        entry -> PlayerDto.builder().id(Long.valueOf(entry.getKey())).build(),
                        Map.Entry::getValue
                ));
        return new CreateRoomParam(
                request.getId(),
                request.getName(),
                request.getIsPublic(),
                request.getCreationDate(),
                request.getTotalScoreToWin(),
                request.getGameStates(),
                totalScores,
                request.getNumPlayers(),
                request.getOwner()
        );
    }

    public RoomDto toDto(RoomEntity entity) {
        return RoomDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .creationDate(entity.getCreationDate())
                .finishedDate(entity.getFinishedDate())
                .numPlayers(entity.getNumPlayers())
                .build();
    }

    public RoomDetailsDto toDetailsDto(RoomEntity entity) {
        return RoomDetailsDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .creationDate(entity.getCreationDate())
                .finishedDate(entity.getFinishedDate())
                .numPlayers(entity.getNumPlayers())
                .gameStates(gameStateMapper.toDtos(entity.getGameStates()))
                .totalScores(scoreTotalMapper.toDtos(entity.getTotalScores()))
                .build();
    }

    public RoomEntity toEntity(CreateRoomParam param, List<GameStateEntity> gameStates) {
        return RoomEntity.builder()
                .id(param.id())
                .name(param.name())
                .isPublic(param.isPublic())
                .creationDate(param.creationDate())
                .finishedDate(LocalDateTime.now())
                .totalScoreToWin(param.totalScoreToWin())
                .numPlayers(param.numPlayers())
                .gameStates(gameStates)
                .owner(PlayerEntity.builder().id(param.owner().getId()).build())
                .build();
    }
}
