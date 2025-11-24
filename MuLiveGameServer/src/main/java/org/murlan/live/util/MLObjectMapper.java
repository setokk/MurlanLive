package org.murlan.live.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

/**
 * MuLive (ML) custom object mapper
 */
public class MLObjectMapper extends ObjectMapper {
    public MLObjectMapper() {
        super();
        registerModule(new JavaTimeModule());
        enable(SerializationFeature.WRITE_ENUMS_USING_INDEX);
    }
}
