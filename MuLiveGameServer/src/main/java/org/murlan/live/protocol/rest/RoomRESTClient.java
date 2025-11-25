package org.murlan.live.protocol.rest;

import org.glassfish.grizzly.http.util.HttpStatus;
import org.murlan.live.game.logic.Room;
import org.murlan.live.protocol.config.ProtocolConfig;
import org.murlan.live.util.MLObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class RoomRESTClient {
    private static final String ENDPOINT = "/api/rooms";
    private final HttpClient httpClient;
    private final ProtocolConfig config;
    private final MLObjectMapper objectMapper;

    public RoomRESTClient(ProtocolConfig config, MLObjectMapper objectMapper) {
        this.httpClient = HttpClient.newHttpClient();
        this.config = config;
        this.objectMapper = objectMapper;
    }

    public boolean createRoom(Room room) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(room)))
                .header("Content-Type", "application/json")
                .uri(URI.create(config.getProtocol_um_server_host() + ENDPOINT + "/create"))
                .build();
        var response = httpClient.send(request, HttpResponse.BodyHandlers.discarding());
        return response.statusCode() == HttpStatus.OK_200.getStatusCode();
    }
}
