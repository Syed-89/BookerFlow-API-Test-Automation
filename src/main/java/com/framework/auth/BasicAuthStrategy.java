package com.framework.auth;

import com.framework.config.AuthType;
import io.restassured.specification.RequestSpecification;

/** Sends "Authorization: Basic base64(user:password)" preemptively. */
public class BasicAuthStrategy implements AuthStrategy {

    private final String username;
    private final String password;

    public BasicAuthStrategy(String username, String password) {
        this.username = username;
        this.password = password;
    }

    @Override
    public RequestSpecification apply(RequestSpecification spec) {
        return spec.auth().preemptive().basic(username, password);
    }

    @Override
    public AuthType type() {
        return AuthType.BASIC;
    }
}
