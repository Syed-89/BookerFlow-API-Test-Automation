package com.framework.serialization;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

/**
 * One shared, pre-configured Jackson mapper used by REST Assured, the data readers
 * and anything else that (de)serializes JSON, so behaviour is identical everywhere.
 *
 * - LocalDate  <-> "yyyy-MM-dd"
 * - null fields are omitted (makes partial PATCH payloads trivial)
 * - unknown response fields are ignored (tolerant reader)
 */
public final class ObjectMapperFactory {

    private static final ObjectMapper MAPPER = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .setSerializationInclusion(JsonInclude.Include.NON_NULL);

    private ObjectMapperFactory() {
    }

    public static ObjectMapper getInstance() {
        return MAPPER;
    }
}
