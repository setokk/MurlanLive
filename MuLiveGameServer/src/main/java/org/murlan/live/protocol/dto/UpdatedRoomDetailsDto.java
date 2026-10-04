package org.murlan.live.protocol.dto;

public record UpdatedRoomDetailsDto(String roomName, Short totalScoreToWin, Long turnDurationInSeconds) {
}

