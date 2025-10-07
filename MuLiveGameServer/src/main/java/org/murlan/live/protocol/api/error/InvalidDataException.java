package org.murlan.live.protocol.api.error;

public class InvalidDataException extends RuntimeException {
    public InvalidDataException() {
        super("Invalid data");
    }
}
