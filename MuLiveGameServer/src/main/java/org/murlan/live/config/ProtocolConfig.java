package org.murlan.live.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ProtocolConfig {
    private String protocol_version;
    private String protocol_name;
    private String protocol_host;
    private int protocol_port;
    private String protocol_delimiter;
}