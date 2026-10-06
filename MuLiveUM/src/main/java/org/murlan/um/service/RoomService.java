package org.murlan.um.service;

import lombok.RequiredArgsConstructor;
import org.murlan.um.core.logic.GameStateEnum;
import org.murlan.um.error.BusinessLogicException;
import org.murlan.um.model.GameStateEntity;
import org.murlan.um.model.RoomEntity;
import org.murlan.um.model.ScoreTotalEntity;
import org.murlan.um.model.dto.PlayerDto;
import org.murlan.um.model.dto.RankRatingDto;
import org.murlan.um.model.dto.RoomDetailsDto;
import org.murlan.um.model.dto.RoomDto;
import org.murlan.um.repository.RoomRepository;
import org.murlan.um.repository.ScoreTotalRepository;
import org.murlan.um.service.mapper.GameStateMapper;
import org.murlan.um.service.mapper.RoomMapper;
import org.murlan.um.service.mapper.ScoreTotalMapper;
import org.murlan.um.service.param.CreateRoomParam;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoomService {
    private final RoomRepository roomRepository;
    private final ScoreTotalRepository scoreTotalRepository;
    private final RoomMapper roomMapper;
    private final GameStateMapper gameStateMapper;
    private final ScoreTotalMapper scoreTotalMapper;
    private final AuthService authService;
    private final PlayerRatingService playerRatingService;

    @Value("${mulive.pagination.size}")
    private int pageSize;

    @Transactional
    public RoomDto createRoom(CreateRoomParam param) {
        List<GameStateEntity> gameStates = param.gameStates().stream()
                .map(gameStateMapper::toEntity)
                .toList();

        RoomEntity room = roomMapper.toEntity(param, gameStates);
        gameStates.forEach(gs -> gs.setRoom(room));
        RoomEntity savedRoom = roomRepository.save(room);

        Map.Entry<PlayerDto, Short> winnerEntry = param.totalScores().entrySet().iterator().next();

        List<ScoreTotalEntity> totalScores = param.totalScores().entrySet().stream()
                .map(totalScore -> {
                    boolean isWinner = winnerEntry.getKey().equals(totalScore.getKey());
                    return scoreTotalMapper.toEntity(totalScore, savedRoom, isWinner);
                })
                .toList();
        scoreTotalRepository.saveAll(totalScores);

        Map<Long, RankRatingDto> rankRatingsByPlayerId = null;
        if (GameStateEnum.FINISHED.equals(gameStates.getLast().getState())) {
            // get first score in case a game was interrupted in the middle
            // which means we cannot use totalMaxScore of the room to update ranks
            // getting the first score will always result in the correct ranking since it is the score of the winner
            Short firstScore = winnerEntry.getValue();

            Map<Long, Short> pointsByPlayerId = param.totalScores().entrySet().stream()
                    .collect(Collectors.toMap(e -> e.getKey().getId(), Map.Entry::getValue));
            rankRatingsByPlayerId = playerRatingService.updatePlayerRatings(pointsByPlayerId, firstScore);
        }

        return roomMapper.toDto(savedRoom, rankRatingsByPlayerId);
    }

    public List<RoomDto> getRooms(int pageNumber, long playerId) {
        Pageable pageable = PageRequest.of(
                pageNumber, pageSize,
                Sort.by("creationDate").descending()
        );

        List<RoomEntity> rooms = authService.getAuthenticatedUser().getId().equals(playerId)
                ? roomRepository.findAllRoomsByPlayerId(playerId, pageable)
                : roomRepository.findPublicRoomsByPlayerId(playerId, pageable);

        return rooms.stream()
                .map(roomMapper::toDto)
                .toList();
    }

    public RoomDetailsDto getRoomDetails(UUID roomId) {
        RoomEntity room = roomRepository.findById(roomId)
                .orElseThrow(() -> new BusinessLogicException(HttpStatus.NOT_FOUND, "Room with id: " + roomId + " not found"));

        if (!room.getIsPublic()) {
            PlayerDto player = authService.getAuthenticatedUser();

            boolean isPlayerNotInRoom = room.getTotalScores().stream().noneMatch(ts -> player.getId().equals(ts.getId().getPlayerId()));
            if (isPlayerNotInRoom) {
                throw new BusinessLogicException(
                        HttpStatus.FORBIDDEN,
                        "Access denied to room with id: " + roomId
                );
            }
        }

        return roomMapper.toDetailsDto(room);
    }
}
