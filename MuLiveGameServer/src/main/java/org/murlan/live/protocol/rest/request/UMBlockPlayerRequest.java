package org.murlan.live.protocol.rest.request;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class UMBlockPlayerRequest {
    private final Long playerToBlockId;
}
