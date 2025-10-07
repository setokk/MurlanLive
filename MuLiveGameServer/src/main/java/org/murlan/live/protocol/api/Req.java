package org.murlan.live.protocol.api;

public interface Req {
    default String getJWT() {
        return "";
    }
    void setJWT(String jwt);

    /**
     * The start index for messagesPart array (the actual data).
     * See {@link org.murlan.live.protocol.util.Parser} for more information
     * @return the starting index
     */
    default int startIndex() {
        return 2;
    }
}
