package org.murlan.live.protocol.api;

import org.murlan.live.protocol.api.error.InvalidDataException;

import static org.murlan.live.protocol.util.Parser.MIN_NUM_VALUES;

public interface Req {
    /**
     * Returns the start index for messagesPart array (the actual data).
     * See {@link org.murlan.live.protocol.util.Parser} for more information
     * @return the starting index
     */
    default int startIndex() {
        return MIN_NUM_VALUES;
    }

    /**
     * Returns the number of fields inside a request body
     * @return the number of fields
     */
    default int numOfFields() {
        return getClass().getDeclaredFields().length;
    }

    /**
     * Validates the request body for every request.
     * <br/>
     * <b>IMPORTANT:</b> Always use it before assigning fields in the request constructors.
     * @param messageParts the message parts that have been split
     * @throws InvalidDataException when there is a request body error (different length than expected)
     */
    default void validate(String[] messageParts) throws InvalidDataException {
        if (messageParts.length != startIndex() + numOfFields()) {
            throw new InvalidDataException();
        }
    }
}
