package com.framework.client;

import io.restassured.response.Response;

/**
 * Typed wrapper around a REST Assured {@link Response}.
 * Keeps the raw response available for advanced checks while giving tests a
 * strongly-typed {@link #body()}.
 */
public final class ApiResponse<T> {

    private final Response raw;
    private final Class<T> type;

    private ApiResponse(Response raw, Class<T> type) {
        this.raw = raw;
        this.type = type;
    }

    public static <T> ApiResponse<T> of(Response raw, Class<T> type) {
        return new ApiResponse<>(raw, type);
    }

    public int status() {
        return raw.getStatusCode();
    }

    public long timeMs() {
        return raw.getTime();
    }

    public String header(String name) {
        return raw.getHeader(name);
    }

    public Response raw() {
        return raw;
    }

    /** Deserializes the body with the framework's shared ObjectMapper. */
    public T body() {
        if (type == Void.class) {
            return null;
        }
        if (type == String.class) {
            return type.cast(raw.asString());
        }
        return raw.as(type);
    }
}
