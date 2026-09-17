package org.murlan.live.protocol.api;

import org.murlan.live.protocol.api.error.InvalidDataException;
import org.murlan.live.protocol.util.Parser;

import java.math.BigDecimal;
import java.math.BigInteger;

import static org.murlan.live.protocol.util.Parser.MIN_NUM_VALUES;

public interface Req {
    /**
     * Returns the start index for messagesPart array (the actual data).
     * See {@link Parser} for more information
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
     * @throws InvalidDataException when there is a request body error
     */
    default void validate(String[] messageParts) throws InvalidDataException {
        if (messageParts.length != startIndex() + numOfFields()) {
            throw new InvalidDataException();
        }
    }

    /**
     * Post validates for business logic purposes (length of a field, max numeric field etc.)
     * @throws InvalidDataException when the request violates any specified business rule.
     */
    default void postValidate() throws InvalidDataException {
    }

    /**
     * Helper that parses a string into a any number.
     * @param s the string input
     * @param errorValue the error value, in case the conversion fails ({@link NumberFormatException})
     * @return the parsed number
     * @param <T> the number type
     */
    @SuppressWarnings("unchecked")
    default <T extends Number> T parseNumber(String s, T errorValue) {
        try {
            return switch (errorValue) {
                case Short ignored -> (T) Short.valueOf(s);
                case Integer ignored -> (T) Integer.valueOf(s);
                case Long ignored -> (T) Long.valueOf(s);
                case Float ignored -> (T) Float.valueOf(s);
                case Double ignored -> (T) Double.valueOf(s);
                case BigInteger ignored -> (T) new BigInteger(s);
                case BigDecimal ignored -> (T) new BigDecimal(s);
                default -> throw new IllegalStateException("Unexpected value: " + errorValue);
            };
        } catch (NumberFormatException e) {
            return errorValue;
        }
    }
}
