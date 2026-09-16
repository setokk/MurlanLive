package org.murlan.live.game.logic;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public final class PassCounter {
    private int counter;

    public synchronized void reset() {
        this.counter = 0;
    }

    public synchronized void resetAfterEmptyHand() {
        this.counter = -1;
    }

    public synchronized void increment() {
        this.counter++;
    }
}
