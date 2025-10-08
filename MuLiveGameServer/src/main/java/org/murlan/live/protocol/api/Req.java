package org.murlan.live.protocol.api;

import static org.murlan.live.protocol.util.Parser.MIN_NUM_VALUES;

public interface Req {
    /**
     * The start index for messagesPart array (the actual data).
     * See {@link org.murlan.live.protocol.util.Parser} for more information
     * @return the starting index
     */
    default int startIndex() {
        return MIN_NUM_VALUES;
    }
}
