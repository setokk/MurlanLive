package org.murlan.live.protocol.rest;

import org.murlan.live.protocol.config.ProtocolConfig;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class PlayerRESTClient {
    private static final String ENDPOINT = "/api/player";
    private final HttpClient httpClient;
    private final ProtocolConfig config;

    public PlayerRESTClient(ProtocolConfig config) {
        this.httpClient = HttpClient.newHttpClient();
        this.config = config;
    }

    public boolean validateJwt(String jwt) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(config.getProtocol_um_server_host() + ENDPOINT + "/validateJwt"))
                .header("Authorization", "Bearer " + jwt)
                .GET()
                .build();

        HttpResponse<Void> response = httpClient.send(request, HttpResponse.BodyHandlers.discarding());
        return response.statusCode() == 200;
    }
}
