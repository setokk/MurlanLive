package org.murlan.live.protocol.auth;

import org.murlan.live.config.ProtocolConfig;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class AuthHttpClient {
    private final HttpClient httpClient;
    private final ProtocolConfig config;

    public AuthHttpClient(ProtocolConfig config) {
        this.httpClient = HttpClient.newHttpClient();
        this.config = config;
    }

    public boolean validateJwt(String jwt) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(config.getProtocol_um_server_host() + "/api/players/validateJwt"))
                .header("Authorization", "Bearer " + jwt)
                .GET()
                .build();

        HttpResponse<Void> response = httpClient.send(request, HttpResponse.BodyHandlers.discarding());
        return response.statusCode() == 200;
    }
}
