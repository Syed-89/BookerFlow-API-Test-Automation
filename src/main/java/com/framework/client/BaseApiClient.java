package com.framework.client;

import com.framework.auth.AuthStrategy;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import java.util.Map;
import java.util.Objects;

/**
 * Abstract parent of every service client. It owns the HTTP verbs, retry policy and
 * auth wiring; subclasses only describe endpoints and typed inputs/outputs.
 * The auth mechanism is injected (composition), so one client class works with any
 * {@link AuthStrategy}.
 */
public abstract class BaseApiClient {

    protected final AuthStrategy authStrategy;

    protected BaseApiClient(AuthStrategy authStrategy) {
        this.authStrategy = Objects.requireNonNull(authStrategy, "authStrategy");
    }

    /** New authenticated request, built per call (stateless => parallel-safe). */
    protected RequestSpecification newRequest() {
        return authStrategy.apply(RequestSpecFactory.create());
    }

    protected Response get(String path, Map<String, ?> queryParams, Object... pathParams) {
        return RetryExecutor.execute(() -> {
            RequestSpecification spec = newRequest();
            if (queryParams != null && !queryParams.isEmpty()) {
                spec.queryParams(queryParams);
            }
            return spec.get(path, pathParams);
        }, true);
    }

    protected Response post(String path, Object body, Object... pathParams) {
        return RetryExecutor.execute(() -> newRequest().body(body).post(path, pathParams), false);
    }

    protected Response put(String path, Object body, Object... pathParams) {
        return RetryExecutor.execute(() -> newRequest().body(body).put(path, pathParams), true);
    }

    protected Response patch(String path, Object body, Object... pathParams) {
        return RetryExecutor.execute(() -> newRequest().body(body).patch(path, pathParams), false);
    }

    protected Response delete(String path, Object... pathParams) {
        return RetryExecutor.execute(() -> newRequest().delete(path, pathParams), true);
    }
}
