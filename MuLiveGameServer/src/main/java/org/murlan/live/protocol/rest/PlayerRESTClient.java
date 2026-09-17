package org.murlan.live.protocol.rest;

import com.fasterxml.jackson.core.type.TypeReference;
import org.glassfish.grizzly.http.util.HttpStatus;
import org.murlan.live.protocol.config.ProtocolConfig;
import org.murlan.live.protocol.dto.Player;
import org.murlan.live.protocol.rest.request.UMBlockPlayerRequest;
import org.murlan.live.protocol.rest.request.UMUnblockPlayerRequest;
import org.murlan.live.util.MLObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Optional;
import java.util.Set;

public class PlayerRESTClient {
    private static final String ENDPOINT = "/api/players";
    private final HttpClient httpClient;
    private final ProtocolConfig config;
    private final MLObjectMapper objectMapper;

    public PlayerRESTClient(ProtocolConfig config, MLObjectMapper objectMapper) {
        this.httpClient = HttpClient.newHttpClient();
        this.config = config;
        this.objectMapper = objectMapper;
    }

    public boolean validateJwt(String jwt) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(config.getProtocol_um_server_host() + ENDPOINT + "/validate-jwt"))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + jwt)
                .GET()
                .build();

        HttpResponse<Void> response = httpClient.send(request, HttpResponse.BodyHandlers.discarding());
        return response.statusCode() == HttpStatus.OK_200.getStatusCode();
    }

    public Optional<Set<Player>> getBlockedPlayers(String jwt) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(config.getProtocol_um_server_host() + ENDPOINT + "/get-blocked-players"))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + jwt)
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != HttpStatus.OK_200.getStatusCode()) {
            return Optional.empty();
        }

        return Optional.of(objectMapper.readValue(response.body(), new TypeReference<>() {}));
    }

    public Optional<Player> blockPlayer(String jwt, Player playerToBlock) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(config.getProtocol_um_server_host() + ENDPOINT + "/block-player"))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + jwt)
                .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(new UMBlockPlayerRequest(playerToBlock.getId()))))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != HttpStatus.OK_200.getStatusCode()) {
            return Optional.empty();
        }

        return Optional.of(objectMapper.readValue(response.body(), new TypeReference<>() {}));
    }

    public Optional<Player> unblockPlayer(String jwt, Player playerToUnblock) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(config.getProtocol_um_server_host() + ENDPOINT + "/unblock-player"))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + jwt)
                .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(new UMUnblockPlayerRequest(playerToUnblock.getId()))))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != HttpStatus.OK_200.getStatusCode()) {
            return Optional.empty();
        }

        return Optional.of(objectMapper.readValue(response.body(), new TypeReference<>() {}));
    }
}
