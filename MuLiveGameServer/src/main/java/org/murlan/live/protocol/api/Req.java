package org.murlan.live.protocol.api;

public sealed interface Req permits GameStateReq, PlayHandReq, PassReq, SurrenderReq {
    default String getJWT() {
        return "";
    }
    void setJWT(String jwt);
}
