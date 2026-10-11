package com.framework.config;

import org.aeonbits.owner.Config;
import org.aeonbits.owner.Config.LoadPolicy;
import org.aeonbits.owner.Config.LoadType;
import org.aeonbits.owner.Config.Sources;

/**
 * Typed view of all framework settings.
 * Resolution order (highest wins): -D system properties / env vars,
 * config/{env}.properties, config/default.properties, @DefaultValue.
 */
@LoadPolicy(LoadType.MERGE)
@Sources({"classpath:config/${env}.properties", "classpath:config/default.properties"})
public interface FrameworkConfig extends Config {

    @Key("base.url")
    String baseUrl();

    @Key("auth.type")
    @DefaultValue("TOKEN")
    AuthType authType();

    @Key("auth.username")
    String authUsername();

    @Key("auth.password")
    String authPassword();

    @Key("auth.token.ttl.seconds")
    @DefaultValue("540")
    int authTokenTtlSeconds();

    @Key("http.connection.timeout.ms")
    @DefaultValue("10000")
    int connectionTimeoutMs();

    @Key("http.socket.timeout.ms")
    @DefaultValue("30000")
    int socketTimeoutMs();

    @Key("retry.max.attempts")
    @DefaultValue("3")
    int retryMaxAttempts();

    @Key("retry.delay.ms")
    @DefaultValue("1000")
    long retryDelayMs();

    @Key("test.retry.count")
    @DefaultValue("1")
    int testRetryCount();

    @Key("cleanup.enabled")
    @DefaultValue("true")
    boolean cleanupEnabled();
}
