package org.murlan.live.protocol.rest.request;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class UMUnblockPlayerRequest {
    private final Long playerToUnblockId;
}
