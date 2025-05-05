package org.murlan.live.protocol.message;

public interface Req {
    default String getJWT() {
        return "";
    }
    void setJWT(String jwt);
}
