package org.murlan.live.protocol.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.murlan.live.game.logic.Room;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class RoomRESTClient {
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public RoomRESTClient(ObjectMapper objectMapper) {
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = objectMapper;
    }

    public boolean saveRoom(Room room) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(room)))
                .build();
        httpClient.send(request, HttpResponse.BodyHandlers.discarding());
        return true;
    }
}
