package org.murlan.live.protocol;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ResponseStatus {
    OK(200),
    ERROR(999);

    private final int statusCode;

    @Override
    public String toString() {
        return String.valueOf(statusCode);
    }
}
