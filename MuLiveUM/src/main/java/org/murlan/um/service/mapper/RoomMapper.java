package org.murlan.um.service.mapper;

import lombok.RequiredArgsConstructor;
import org.murlan.um.api.request.CreateRoomRequest;
import org.murlan.um.model.PlayerEntity;
import org.murlan.um.model.RoomEntity;
import org.murlan.um.model.RoomPlayerEntity;
import org.murlan.um.model.dto.PlayerScoreDto;
import org.murlan.um.model.dto.RankRatingDto;
import org.murlan.um.model.dto.RoomDetailsDto;
import org.murlan.um.model.dto.RoomDto;
import org.murlan.um.model.dto.RoomItemDto;
import org.murlan.um.model.pk.RoomPlayerId;
import org.murlan.um.repository.PlayerRepository;
import org.murlan.um.service.param.CreateRoomParam;
import org.murlan.um.util.DateUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public final class RoomMapper {
    private final PlayerRepository playerRepository;

    public CreateRoomParam toParam(CreateRoomRequest request) {
        return new CreateRoomParam(
                request.getId(),
                request.getName(),
                request.getIsPublic(),
                request.getCreationDate(),
                request.getTotalScoreToWin(),
                request.getRoundsPlayed(),
                request.getTotalScores(),
                request.getOwnerId()
        );
    }

    public RoomEntity toEntity(CreateRoomParam param) {
        return RoomEntity.builder()
                .id(UUID.fromString(param.id()))
                .name(param.name())
                .isPublic(param.isPublic())
                .creationDate(param.creationDate())
                .finishedDate(DateUtils.getLocalDateTimeNowUTC())
                .totalScoreToWin(param.totalScoreToWin())
                .roundsPlayed(param.roundsPlayed())
                .owner(new PlayerEntity(param.ownerId()))
                .build();
    }

    public List<RoomPlayerEntity> toRoomPlayerEntities(RoomEntity room, Map<Long, Short> totalScores) {
        List<RoomPlayerEntity> roomPlayers = new ArrayList<>();

        short placement = 1;
        for (Map.Entry<Long, Short> entry : totalScores.entrySet()) {
            Long playerId = entry.getKey();
            Short score = entry.getValue();

            roomPlayers.add(RoomPlayerEntity.builder()
                    .id(new RoomPlayerId(room.getId(), playerId))
                    .room(room)
                    .player(playerRepository.getReferenceById(playerId))
                    .placementIndex(placement)
                    .totalScore(score)
                    .build());

            placement++;
        }
        return roomPlayers;
    }

    public RoomDto toDto(RoomEntity entity, Map<Long, RankRatingDto> rankRatingsByPlayerId) {
        return RoomDto.builder()
                .id(entity.getId().toString())
                .name(entity.getName())
                .creationDate(entity.getCreationDate())
                .finishedDate(entity.getFinishedDate())
                .rankRatingsByPlayerId(rankRatingsByPlayerId)
                .build();
    }

    public RoomItemDto toItemDto(RoomEntity entity) {
        return RoomItemDto.builder()
                .id(entity.getId().toString())
                .name(entity.getName())
                .creationDate(entity.getCreationDate())
                .finishedDate(entity.getFinishedDate())
                .build();
    }

    public RoomDetailsDto toDetailsDto(RoomEntity entity) {
        return RoomDetailsDto.builder()
                .id(entity.getId().toString())
                .name(entity.getName())
                .creationDate(entity.getCreationDate())
                .finishedDate(entity.getFinishedDate())
                .totalScoreToWin(entity.getTotalScoreToWin())
                .roundsPlayed(entity.getRoundsPlayed())
                .playerScores(toPlayerScores(entity.getRoomPlayers()))
                .build();
    }

    private List<PlayerScoreDto> toPlayerScores(List<RoomPlayerEntity> entities) {
        return entities.stream()
                .sorted(Comparator.comparingInt(RoomPlayerEntity::getPlacementIndex))
                .map(rp -> new PlayerScoreDto(
                        rp.getId().getPlayerId(),
                        rp.getPlayer().getUsername(),
                        rp.getPlayer().getProfileIcon().getId(),
                        rp.getTotalScore())
                )
                .toList();
    }
}
