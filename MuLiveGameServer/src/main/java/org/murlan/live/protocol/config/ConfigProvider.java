package org.murlan.live.protocol.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.snakeyaml.engine.v2.api.Load;
import org.snakeyaml.engine.v2.api.LoadSettings;
import org.snakeyaml.engine.v2.env.EnvConfig;

import java.io.InputStream;
import java.util.Map;
import java.util.Optional;

public class ConfigProvider {
    private static ProtocolConfig protocolConfig;
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static ProtocolConfig getProtocolConfig() {
        if (protocolConfig == null) {
            reloadProtocolConfig();
        }
        return protocolConfig;
    }

    public static void reloadProtocolConfig() {
        InputStream inputStream = ConfigProvider.class.getClassLoader().getResourceAsStream("org/murlan/live/protocol-config.yml");

        EnvConfig envConfig = new EnvConfig() {
            @Override
            public Optional<String> getValueFor(String name, String separator, String value, String environment) {
                return Optional.ofNullable(System.getenv(name));
            }
        };
        LoadSettings settings = LoadSettings.builder()
                .setEnvConfig(Optional.of(envConfig))
                .build();

        Load load = new Load(settings);
        Map<String, Object> yamlMap = (Map<String, Object>) load.loadFromInputStream(inputStream);
        protocolConfig = objectMapper.convertValue(yamlMap, ProtocolConfig.class);
    }
}
