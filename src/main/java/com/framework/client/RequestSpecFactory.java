package com.framework.client;

import com.framework.config.ConfigManager;
import com.framework.config.FrameworkConfig;
import com.framework.serialization.ObjectMapperFactory;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.config.HttpClientConfig;
import io.restassured.config.ObjectMapperConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.http.ContentType;
import io.restassured.mapper.ObjectMapperType;
import io.restassured.specification.RequestSpecification;

/**
 * Builds the base request every client starts from: base URI, JSON headers, timeouts,
 * the shared Jackson mapper, log filter and the Allure filter.
 * A fresh spec is created per request, so nothing is shared between parallel threads.
 */
public final class RequestSpecFactory {

    private RequestSpecFactory() {
    }

    public static RequestSpecification create() {
        FrameworkConfig cfg = ConfigManager.get();

        RequestSpecification spec = new RequestSpecBuilder()
                .setBaseUri(cfg.baseUrl())
                .setContentType(ContentType.JSON)
                .setAccept(ContentType.JSON)
                .addFilter(new ApiLoggingFilter())
                .addFilter(new AllureRestAssured())
                .build();

        return RestAssured.given().config(restAssuredConfig(cfg)).spec(spec);
    }

    private static RestAssuredConfig restAssuredConfig(FrameworkConfig cfg) {
        return RestAssuredConfig.config()
                .objectMapperConfig(ObjectMapperConfig.objectMapperConfig()
                        .defaultObjectMapperType(ObjectMapperType.JACKSON_2)
                        .jackson2ObjectMapperFactory((type, charset) -> ObjectMapperFactory.getInstance()))
                .httpClient(HttpClientConfig.httpClientConfig()
                        .setParam("http.connection.timeout", cfg.connectionTimeoutMs())
                        .setParam("http.socket.timeout", cfg.socketTimeoutMs()));
    }
}
