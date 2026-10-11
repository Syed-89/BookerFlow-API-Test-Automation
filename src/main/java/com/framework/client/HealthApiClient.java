package com.framework.client;

import com.framework.auth.NoAuthStrategy;
import io.qameta.allure.Step;

/** Client for the /ping health endpoint. */
public class HealthApiClient extends BaseApiClient {

    public HealthApiClient() {
        super(new NoAuthStrategy());
    }

    @Step("Ping service")
    public ApiResponse<String> ping() {
        return ApiResponse.of(get(Endpoints.PING, null), String.class);
    }
}
