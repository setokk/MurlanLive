package org.murlan.live.protocol.config;

import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;

public class ConfigProvider {
    private static ProtocolConfig protocolConfig;

    public static ProtocolConfig getProtocolConfig() {
        if (protocolConfig == null) {
            reloadProtocolConfig();
        }
        return protocolConfig;
    }

    public static void reloadProtocolConfig() {
        InputStream inputStream = ConfigProvider.class.getClassLoader().getResourceAsStream("org/murlan/live/protocol-config.yml");
        protocolConfig = new Yaml().loadAs(inputStream, ProtocolConfig.class);
    }
}
