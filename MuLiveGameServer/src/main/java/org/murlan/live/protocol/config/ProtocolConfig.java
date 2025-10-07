package org.murlan.live.protocol.config;

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
    private String protocol_list_delimiter;
    private String protocol_um_server_host;
}