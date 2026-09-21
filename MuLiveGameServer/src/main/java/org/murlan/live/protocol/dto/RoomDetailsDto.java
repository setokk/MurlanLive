package org.murlan.live.protocol.dto;

public record RoomDetailsDto(String roomName, Short totalScoreToWin, Long turnDurationSeconds) {
    public RoomDetailsDto(String roomName, short totalScoreToWin, long turnDurationSeconds) {
        this(roomName,
                totalScoreToWin == Short.MIN_VALUE ? null : totalScoreToWin,
                turnDurationSeconds == Long.MIN_VALUE ? null : turnDurationSeconds
        );
    }
}

