package org.murlan.live.session;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class PlayerInfo {
    private String id;
    private String username;
    private String creationDate;

    private String jwtToken;

    public PlayerInfo(String jwtToken) {
        this.jwtToken = jwtToken;

    }
}
