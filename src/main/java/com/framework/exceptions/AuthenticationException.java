package com.framework.exceptions;

/** Raised when an auth token cannot be obtained. */
public class AuthenticationException extends FrameworkException {
    public AuthenticationException(String message) {
        super(message);
    }
}
