package com.framework.auth;

import com.framework.config.AuthType;
import com.framework.config.ConfigManager;
import com.framework.config.FrameworkConfig;

/** Factory: maps an {@link AuthType} to its {@link AuthStrategy} implementation. */
public final class AuthStrategyFactory {

    private AuthStrategyFactory() {
    }

    public static AuthStrategy create(AuthType type) {
        FrameworkConfig cfg = ConfigManager.get();
        return switch (type) {
            case TOKEN -> TokenAuthStrategy.managed();
            case BASIC -> new BasicAuthStrategy(cfg.authUsername(), cfg.authPassword());
            case NONE -> new NoAuthStrategy();
        };
    }

    /** Strategy selected by the {@code auth.type} configuration key. */
    public static AuthStrategy fromConfig() {
        return create(ConfigManager.get().authType());
    }
}
