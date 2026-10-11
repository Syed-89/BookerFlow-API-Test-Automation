package com.framework.models.auth;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Objects;

/**
 * Response body of POST /auth.
 * Note: this API answers HTTP 200 for bad credentials with {"reason":"Bad credentials"}.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class AuthResponse {
    private String token;
    private String reason;

    public AuthResponse() {
    }

    public AuthResponse(String token, String reason) {
        this.token = token;
        this.reason = reason;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AuthResponse that = (AuthResponse) o;
        return Objects.equals(token, that.token) && Objects.equals(reason, that.reason);
    }

    @Override
    public int hashCode() {
        return Objects.hash(token, reason);
    }

    @Override
    public String toString() {
        return "AuthResponse{token='[hidden]', reason='" + reason + "'}";
    }

    public static final class Builder {
        private String token;
        private String reason;

        public Builder token(String token) {
            this.token = token;
            return this;
        }

        public Builder reason(String reason) {
            this.reason = reason;
            return this;
        }

        public AuthResponse build() {
            return new AuthResponse(token, reason);
        }
    }
}
