package com.framework.auth;

import com.framework.config.AuthType;
import io.restassured.specification.RequestSpecification;

/**
 * Strategy abstraction: HOW a request gets authenticated.
 * Clients depend on this interface only, so adding OAuth2/API-key later means adding
 * one class and one factory case - no client changes (Open/Closed principle).
 */
public interface AuthStrategy {

    RequestSpecification apply(RequestSpecification spec);

    AuthType type();
}
