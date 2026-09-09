package org.murlan.live.game.logic;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public final class PassCounter {
    private int counter;

    public void reset() {
        this.counter = 0;
    }

    public void increment() {
        this.counter++;
    }
}
