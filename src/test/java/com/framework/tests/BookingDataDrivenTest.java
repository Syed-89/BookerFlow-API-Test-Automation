package com.framework.tests;

import com.framework.base.BaseTest;
import com.framework.client.ApiResponse;
import com.framework.dataproviders.BookingDataProviders;
import com.framework.dataproviders.InvalidBookingScenario;
import com.framework.models.booking.Booking;
import com.framework.models.booking.BookingCreated;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import org.testng.annotations.Test;

import static com.framework.assertions.BookingAssertions.assertBookingEquals;
import static com.framework.assertions.BookingAssertions.assertStatus;
import static com.framework.base.TestGroups.NEGATIVE;
import static com.framework.base.TestGroups.REGRESSION;

@Epic("Restful Booker API")
@Feature("Data-driven booking tests")
public class BookingDataDrivenTest extends BaseTest {

    @Test(dataProvider = "validBookings", dataProviderClass = BookingDataProviders.class,
            groups = REGRESSION, description = "Bookings from JSON data are created correctly")
    @Severity(SeverityLevel.NORMAL)
    public void shouldCreateBookingFromDataSet(Booking booking) {
        ApiResponse<BookingCreated> response = bookingClient.createBooking(booking);

        assertStatus(response, 200);
        assertBookingEquals(response.body().getBooking(), booking);
    }

    @Test(dataProvider = "invalidBookings", dataProviderClass = BookingDataProviders.class,
            groups = NEGATIVE, description = "Invalid payloads are rejected with the documented status")
    @Severity(SeverityLevel.NORMAL)
    public void shouldRejectInvalidPayload(InvalidBookingScenario scenario) {
        ApiResponse<String> response = bookingClient.createBookingRaw(scenario.getPayload());

        assertStatus(response, scenario.getExpectedStatus());
    }
}
