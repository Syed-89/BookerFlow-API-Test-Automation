package com.framework.auth;

import com.framework.config.AuthType;
import io.restassured.specification.RequestSpecification;

import java.util.Objects;
import java.util.function.Supplier;

/** Sends the token as the "token" cookie (what Restful-Booker expects for write calls). */
public class TokenAuthStrategy implements AuthStrategy {

    private static final String COOKIE_NAME = "token";

    private final Supplier<String> tokenSupplier;

    public TokenAuthStrategy(Supplier<String> tokenSupplier) {
        this.tokenSupplier = Objects.requireNonNull(tokenSupplier);
    }

    /** Strategy backed by the shared, cached {@link TokenManager}. */
    public static TokenAuthStrategy managed() {
        return new TokenAuthStrategy(() -> TokenManager.getInstance().getToken());
    }

    @Override
    public RequestSpecification apply(RequestSpecification spec) {
        return spec.cookie(COOKIE_NAME, tokenSupplier.get());
    }

    @Override
    public AuthType type() {
        return AuthType.TOKEN;
    }
}
