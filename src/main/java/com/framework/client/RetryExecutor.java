package com.framework.client;

import com.framework.config.ConfigManager;
import com.framework.config.FrameworkConfig;
import io.restassured.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;
import java.util.function.Supplier;

/**
 * Transport-level resilience: retries on gateway errors (502/503/504, typical of Heroku
 * cold starts) and on connection exceptions. Only idempotent calls are retried so a
 * POST can never create duplicates. Assertion failures are NOT retried here.
 */
public final class RetryExecutor {

    private static final Logger LOG = LoggerFactory.getLogger(RetryExecutor.class);
    private static final Set<Integer> RETRYABLE_STATUS = Set.of(502, 503, 504);

    private RetryExecutor() {
    }

    public static Response execute(Supplier<Response> call, boolean idempotent) {
        FrameworkConfig cfg = ConfigManager.get();
        int maxAttempts = idempotent ? Math.max(1, cfg.retryMaxAttempts()) : 1;
        Response response = null;

        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            try {
                response = call.get();
                if (!RETRYABLE_STATUS.contains(response.getStatusCode())) {
                    return response;
                }
                LOG.warn("Attempt {}/{} got HTTP {}", attempt, maxAttempts, response.getStatusCode());
            } catch (RuntimeException e) {
                if (attempt == maxAttempts) {
                    throw e;
                }
                LOG.warn("Attempt {}/{} failed: {}", attempt, maxAttempts, e.getMessage());
            }
            if (attempt < maxAttempts) {
                pause(cfg.retryDelayMs() * attempt);
            }
        }
        return response;
    }

    private static void pause(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
