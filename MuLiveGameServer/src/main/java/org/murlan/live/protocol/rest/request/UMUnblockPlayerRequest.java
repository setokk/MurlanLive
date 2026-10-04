package org.murlan.live.protocol.rest.request;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UMUnblockPlayerRequest {
    private final Long playerToUnblockId;
}
