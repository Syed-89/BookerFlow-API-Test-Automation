package com.framework.auth;

import com.framework.config.AuthType;
import io.restassured.specification.RequestSpecification;

/** Null-object strategy: sends the request unauthenticated. */
public class NoAuthStrategy implements AuthStrategy {

    @Override
    public RequestSpecification apply(RequestSpecification spec) {
        return spec;
    }

    @Override
    public AuthType type() {
        return AuthType.NONE;
    }
}
