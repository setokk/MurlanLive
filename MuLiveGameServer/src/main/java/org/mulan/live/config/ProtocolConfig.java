package org.mulan.live.config;

import lombok.*;
import lombok.experimental.Accessors;

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
}