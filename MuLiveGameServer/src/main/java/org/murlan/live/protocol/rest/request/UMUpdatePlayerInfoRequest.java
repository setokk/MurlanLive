package org.murlan.live.protocol.rest.request;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public final class UMUpdatePlayerInfoRequest {
    private final long profileIconId;
}
