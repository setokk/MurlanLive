package org.murlan.live.endpoint;

import jakarta.websocket.CloseReason;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MuliveCloseReason {
    FORBIDDEN(
            CloseReason.CloseCodes.VIOLATED_POLICY,
            "Forbidden"
    ),

    INVALID_JWT(
            CloseReason.CloseCodes.VIOLATED_POLICY,
            "Invalid JWT"
    ),

    JWT_SESSION_ALREADY_EXISTS(
            CloseReason.CloseCodes.VIOLATED_POLICY,
            "JWT session already exists"
    ),

    REQUEST_BODY_ERROR(
            CloseReason.CloseCodes.NOT_CONSISTENT,
            "Request body error"
    ),

    NO_ACTIVE_SESSION(
            CloseReason.CloseCodes.UNEXPECTED_CONDITION,
            "No active session found"
    );

    private final CloseReason.CloseCode code;
    private final String message;

    public CloseReason create() {
        return new CloseReason(code, message);
    }
}
