package org.murlan.live.protocol.dto;

public record RoomDetailsDto(String roomName, Short totalScoreToWin, Long turnDurationInSeconds) {
    public RoomDetailsDto(String roomName, short totalScoreToWin, long turnDurationInSeconds) {
        this(roomName,
                totalScoreToWin == Short.MIN_VALUE ? null : totalScoreToWin,
                turnDurationInSeconds == Long.MIN_VALUE ? null : turnDurationInSeconds
        );
    }
}

