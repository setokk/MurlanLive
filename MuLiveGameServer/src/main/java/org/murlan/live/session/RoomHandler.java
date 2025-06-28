package org.murlan.live.session;

import lombok.Getter;
import org.murlan.live.session.player.PlayerSession;

import java.util.concurrent.ConcurrentHashMap;

@Getter
public class RoomHandler {
    private final ConcurrentHashMap<PlayerSession, String> sessionToRoomMap = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Room> rooms = new ConcurrentHashMap<>();
}
