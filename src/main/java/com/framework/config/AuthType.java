package com.framework.config;

/** Authentication mechanisms supported by the framework. */
public enum AuthType {
    /** Token obtained from POST /auth and sent as the "token" cookie. */
    TOKEN,
    /** HTTP Basic authentication header. */
    BASIC,
    /** No credentials (used for negative / public-endpoint tests). */
    NONE
}
