package org.murlan.live.protocol.rest;

import org.glassfish.grizzly.http.util.HttpStatus;
import org.murlan.live.game.logic.Room;
import org.murlan.live.util.MLObjectMapper;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class RoomRESTClient {
    private final HttpClient httpClient;
    private final MLObjectMapper objectMapper;

    public RoomRESTClient(MLObjectMapper objectMapper) {
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = objectMapper;
    }

    public boolean createRoom(Room room) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .PUT(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(room)))
                .build();
        var response = httpClient.send(request, HttpResponse.BodyHandlers.discarding());
        return response.statusCode() == HttpStatus.OK_200.getStatusCode();
    }
}
