package org.murlan.live;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GameScenario {
    private String name;
    private List<String> players;
    private String room_owner;
}
