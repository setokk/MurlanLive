package org.murlan.live.protocol.rest;

import org.glassfish.grizzly.http.util.HttpStatus;
import org.murlan.live.protocol.config.ProtocolConfig;
import org.murlan.live.protocol.dto.PlayerDto;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class PlayerRESTClient {
    private static final String ENDPOINT = "/api/players";
    private final HttpClient httpClient;
    private final ProtocolConfig config;

    public PlayerRESTClient(ProtocolConfig config) {
        this.httpClient = HttpClient.newHttpClient();
        this.config = config;
    }

    public boolean validateJwt(String jwt) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(config.getProtocol_um_server_host() + ENDPOINT + "/validateJwt"))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + jwt)
                .GET()
                .build();

        HttpResponse<Void> response = httpClient.send(request, HttpResponse.BodyHandlers.discarding());
        return response.statusCode() == HttpStatus.OK_200.getStatusCode();
    }

    public HttpResponse<String> registerPlayer(PlayerDto playerDto) throws InterruptedException, IOException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(config.getProtocol_um_server_host() + ENDPOINT + "/register"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(
                        String.format("{\"username\":\"%s\",\"password\":\"%s\"}", playerDto.getUsername(), playerDto.getUsername())
                ))
                .build();

        return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }

    public HttpResponse<String> loginPlayer(PlayerDto playerDto) throws InterruptedException, IOException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(config.getProtocol_um_server_host() + ENDPOINT + "/login"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(
                        String.format("{\"username\":\"%s\",\"password\":\"%s\"}", playerDto.getUsername(), playerDto.getUsername())
                ))
                .build();

        return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }
}
