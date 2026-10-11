package com.framework.tests;

import com.framework.auth.AuthStrategyFactory;
import com.framework.base.BaseTest;
import com.framework.client.ApiResponse;
import com.framework.client.AuthApiClient;
import com.framework.client.BookingApiClient;
import com.framework.config.AuthType;
import com.framework.config.ConfigManager;
import com.framework.data.BookingDataFactory;
import com.framework.models.auth.AuthRequest;
import com.framework.models.auth.AuthResponse;
import com.framework.models.booking.Booking;
import com.framework.models.booking.BookingCreated;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static com.framework.assertions.BookingAssertions.assertStatus;
import static com.framework.base.TestGroups.NEGATIVE;
import static com.framework.base.TestGroups.SMOKE;
import static org.assertj.core.api.Assertions.assertThat;

@Epic("Restful Booker API")
@Feature("Authentication")
public class AuthenticationTest extends BaseTest {

    private final AuthApiClient authClient = new AuthApiClient();

    @Test(groups = SMOKE, description = "Valid credentials return a token")
    @Story("Token authentication")
    @Severity(SeverityLevel.BLOCKER)
    public void validCredentialsReturnToken() {
        AuthRequest request = AuthRequest.builder()
                .username(ConfigManager.get().authUsername())
                .password(ConfigManager.get().authPassword())
                .build();

        ApiResponse<AuthResponse> response = authClient.createToken(request);

        assertStatus(response, 200);
        assertThat(response.body().getToken()).isNotBlank();
    }

    @Test(groups = NEGATIVE, description = "Bad credentials are reported in the body (API answers HTTP 200)")
    @Story("Token authentication")
    @Severity(SeverityLevel.CRITICAL)
    public void invalidCredentialsReturnReason() {
        AuthRequest request = AuthRequest.builder().username("admin").password("definitely-wrong").build();

        ApiResponse<AuthResponse> response = authClient.createToken(request);

        assertStatus(response, 200);
        assertThat(response.body().getToken()).isNull();
        assertThat(response.body().getReason()).isEqualTo("Bad credentials");
    }

    @DataProvider(name = "authTypes")
    public static Object[][] authTypes() {
        return new Object[][]{{AuthType.TOKEN}, {AuthType.BASIC}};
    }

    @Test(dataProvider = "authTypes", groups = SMOKE,
            description = "Each supported auth mechanism is accepted on protected endpoints")
    @Story("Authorization on write calls")
    @Severity(SeverityLevel.CRITICAL)
    public void protectedEndpointAcceptsEachAuthMechanism(AuthType type) {
        BookingCreated created = createBooking(BookingDataFactory.validBooking());
        BookingApiClient client = new BookingApiClient(AuthStrategyFactory.create(type));
        Booking update = BookingDataFactory.validBooking();

        ApiResponse<Booking> response = client.updateBooking(created.getBookingid(), update);

        assertStatus(response, 200);
    }
}
