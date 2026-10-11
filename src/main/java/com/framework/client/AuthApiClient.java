package com.framework.client;

import com.framework.auth.NoAuthStrategy;
import com.framework.models.auth.AuthRequest;
import com.framework.models.auth.AuthResponse;
import io.qameta.allure.Step;

/** Client for the /auth endpoint (itself unauthenticated). */
public class AuthApiClient extends BaseApiClient {

    public AuthApiClient() {
        super(new NoAuthStrategy());
    }

    @Step("Request auth token")
    public ApiResponse<AuthResponse> createToken(AuthRequest request) {
        return ApiResponse.of(post(Endpoints.AUTH, request), AuthResponse.class);
    }
}
