package org.murlan.live.protocol.rest;

import org.glassfish.grizzly.http.util.HttpStatus;
import org.murlan.live.game.logic.Room;
import org.murlan.live.protocol.config.ProtocolConfig;
import org.murlan.live.protocol.dto.um.UMRoomDto;
import org.murlan.live.protocol.rest.request.UMCreateRoomRequest;
import org.murlan.live.util.MLObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Optional;

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

    public Optional<UMRoomDto> createRoom(Room room) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(UMCreateRoomRequest.createFrom(room))))
                .header("Content-Type", "application/json")
                .header(config.getMulive_gameserver_secret_header(), config.getMulive_gameserver_secret_header_val())
                .uri(URI.create(config.getProtocol_um_server_host() + ENDPOINT + "/create"))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != HttpStatus.OK_200.getStatusCode()) {
            return Optional.empty();
        }

        return Optional.of(objectMapper.readValue(response.body(), UMRoomDto.class));
    }
}
