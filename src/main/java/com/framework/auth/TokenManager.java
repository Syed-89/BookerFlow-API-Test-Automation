package com.framework.auth;

import com.framework.client.ApiResponse;
import com.framework.client.AuthApiClient;
import com.framework.config.ConfigManager;
import com.framework.config.FrameworkConfig;
import com.framework.exceptions.AuthenticationException;
import com.framework.models.auth.AuthRequest;
import com.framework.models.auth.AuthResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Thread-safe, lazily refreshed token cache (singleton).
 * One login is shared by all parallel tests; a new one is fetched only after the TTL
 * (double-checked locking so concurrent threads never trigger duplicate logins).
 */
public final class TokenManager {

    private static final Logger LOG = LoggerFactory.getLogger(TokenManager.class);

    private static final class Holder {
        private static final TokenManager INSTANCE = new TokenManager();
    }

    private final ReentrantLock lock = new ReentrantLock();
    private volatile String token;
    private volatile Instant expiresAt = Instant.EPOCH;

    private TokenManager() {
    }

    public static TokenManager getInstance() {
        return Holder.INSTANCE;
    }

    public String getToken() {
        if (isValid()) {
            return token;
        }
        lock.lock();
        try {
            if (!isValid()) {
                refresh();
            }
            return token;
        } finally {
            lock.unlock();
        }
    }

    /** Forces the next call to log in again. */
    public void invalidate() {
        expiresAt = Instant.EPOCH;
    }

    private boolean isValid() {
        return token != null && Instant.now().isBefore(expiresAt);
    }

    private void refresh() {
        FrameworkConfig cfg = ConfigManager.get();
        AuthRequest request = AuthRequest.builder()
                .username(cfg.authUsername())
                .password(cfg.authPassword())
                .build();

        ApiResponse<AuthResponse> response = new AuthApiClient().createToken(request);
        AuthResponse body = response.status() == 200 ? response.body() : null;

        if (body == null || body.getToken() == null || body.getToken().isBlank()) {
            String reason = body != null ? body.getReason() : "HTTP " + response.status();
            throw new AuthenticationException("Unable to obtain auth token: " + reason);
        }
        token = body.getToken();
        expiresAt = Instant.now().plusSeconds(cfg.authTokenTtlSeconds());
        LOG.info("Auth token refreshed (valid for {}s)", cfg.authTokenTtlSeconds());
    }
}
