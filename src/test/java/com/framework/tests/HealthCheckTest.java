package com.framework.tests;

import com.framework.base.BaseTest;
import com.framework.client.ApiResponse;
import com.framework.client.HealthApiClient;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import org.testng.annotations.Test;

import static com.framework.assertions.BookingAssertions.assertStatus;
import static com.framework.base.TestGroups.SMOKE;
import static org.assertj.core.api.Assertions.assertThat;

@Epic("Restful Booker API")
@Feature("Health")
public class HealthCheckTest extends BaseTest {

    private final HealthApiClient healthClient = new HealthApiClient();

    @Test(groups = SMOKE, description = "Service responds to /ping")
    @Severity(SeverityLevel.BLOCKER)
    public void pingReturnsCreated() {
        ApiResponse<String> response = healthClient.ping();

        assertStatus(response, 201);
        assertThat(response.body()).isEqualTo("Created");
    }
}
