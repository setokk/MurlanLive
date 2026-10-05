package org.murlan.um.service;

import lombok.RequiredArgsConstructor;
import org.murlan.um.error.BusinessLogicException;
import org.murlan.um.model.RoomEntity;
import org.murlan.um.model.dto.RankRatingDto;
import org.murlan.um.model.dto.RoomDetailsDto;
import org.murlan.um.model.dto.RoomDto;
import org.murlan.um.model.dto.RoomItemDto;
import org.murlan.um.repository.RoomRepository;
import org.murlan.um.service.mapper.RoomMapper;
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

@Service
@RequiredArgsConstructor
public class RoomService {
    private final RoomRepository roomRepository;
    private final RoomMapper roomMapper;
    private final AuthService authService;
    private final PlayerRatingService playerRatingService;

    @Value("${mulive.pagination.size}")
    private int pageSize;

    @Transactional
    public RoomDto createRoom(CreateRoomParam param) {
        RoomEntity room = roomRepository.save(roomMapper.toEntity(param));
        room.setRoomPlayers(roomMapper.toRoomPlayerEntities(room, param.totalScores()));

        Short winnerScore = param.totalScores().values().iterator().next();
        Map<Long, RankRatingDto> playerRankings = playerRatingService.updatePlayerRatings(param.totalScores(), winnerScore);
        return roomMapper.toDto(room, playerRankings);
    }

    public List<RoomItemDto> getRooms(int pageNumber, long playerId) {
        Pageable pageable = PageRequest.of(
                pageNumber, pageSize,
                Sort.by("creationDate").descending()
        );

        List<RoomEntity> rooms = authService.getAuthenticatedUser().getId().equals(playerId)
                ? roomRepository.findAllRoomsByPlayerId(playerId, pageable)
                : roomRepository.findPublicRoomsByPlayerId(playerId, pageable);

        return rooms.stream()
                .map(roomMapper::toItemDto)
                .toList();
    }

    public RoomDetailsDto getRoomDetails(UUID roomId) {
        RoomEntity room = roomRepository.findById(roomId)
                .orElseThrow(() -> new BusinessLogicException(HttpStatus.NOT_FOUND, "Room with id: " + roomId + " not found"));
        return roomMapper.toDetailsDto(room);
    }
}
