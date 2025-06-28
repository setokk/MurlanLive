package org.murlan.live.session;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class PlayerDto {
    private long id;
    private String username;
    private String creationDate;
    private String jwtToken;

    public PlayerDto(String jwtToken) {
        this.jwtToken = jwtToken;

    }
}
