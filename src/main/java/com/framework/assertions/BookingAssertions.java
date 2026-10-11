package com.framework.assertions;

import com.framework.client.ApiResponse;
import com.framework.models.booking.Booking;
import io.qameta.allure.Step;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.assertj.core.api.Assertions.assertThat;

/** Reusable, Allure-annotated assertions so every check shows up as a named report step. */
public final class BookingAssertions {

    private BookingAssertions() {
    }

    @Step("Verify HTTP status is {expected}")
    public static void assertStatus(ApiResponse<?> response, int expected) {
        assertThat(response.status())
                .as("HTTP status (response body: %s)", response.raw().asString())
                .isEqualTo(expected);
    }

    @Step("Verify booking matches expected data")
    public static void assertBookingEquals(Booking actual, Booking expected) {
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @Step("Verify response matches JSON schema {schemaPath}")
    public static void assertMatchesSchema(ApiResponse<?> response, String schemaPath) {
        response.raw().then().body(matchesJsonSchemaInClasspath(schemaPath));
    }
}
