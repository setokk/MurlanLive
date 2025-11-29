package org.murlan.live;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.glassfish.grizzly.http.util.HttpStatus;
import org.junit.Before;
import org.junit.Test;
import org.murlan.live.protocol.config.ConfigProvider;
import org.murlan.live.protocol.config.ProtocolConfig;
import org.murlan.live.protocol.dto.PlayerDto;
import org.murlan.live.protocol.jwt.JwtUtils;
import org.murlan.live.protocol.rest.PlayerRESTClient;
import org.snakeyaml.engine.v2.api.Load;
import org.snakeyaml.engine.v2.api.LoadSettings;

import java.io.IOException;
import java.io.InputStream;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LiveGameTest {
    private ProtocolConfig config;
    private PlayerRESTClient playerRESTClient;

    @Before
    public void setUp() {
        this.config = ConfigProvider.getProtocolConfig();
        this.playerRESTClient = new PlayerRESTClient(config);
    }

    @Test
    public void gameScenario_1() throws IOException, InterruptedException {
        executeGameScenario(1);
    }

    private void executeGameScenario(int scenarioId) throws IOException, InterruptedException {
        GameScenario gameScenario = loadGameScenario(scenarioId);
        List<PlayerDto> players = registerOrLoginPlayers(gameScenario.getPlayers());
        players.forEach(player -> {
            System.out.println(player.getUsername() + ": " + player.getJwt());
        });
    }

    private GameScenario loadGameScenario(int scenarioId) {
        String gameScenarioYaml = String.format("org/murlan/live/scenarios/game-scenario%s.yml", scenarioId);
        InputStream inputStream = ConfigProvider.class.getClassLoader().getResourceAsStream(gameScenarioYaml);
        Load load = new Load(LoadSettings.builder().build());
        Map<String, Object> yamlMap = (Map<String, Object>) load.loadFromInputStream(inputStream);
        return new ObjectMapper().convertValue(yamlMap, GameScenario.class);
    }

    private List<PlayerDto> registerOrLoginPlayers(List<String> usernames) throws IOException, InterruptedException {
        List<PlayerDto> players = new ArrayList<>(usernames.size());
        for (String username : usernames) {
            PlayerDto playerDto = PlayerDto.builder().username(username).build();
            HttpResponse<String> response = playerRESTClient.registerPlayer(playerDto);
            if (HttpStatus.CONFLICT_409.getStatusCode() == response.statusCode()) {
                response = playerRESTClient.loginPlayer(playerDto);
            }
            players.add(JwtUtils.decodeJWT(response.body()));
        }
        return players;
    }
}
