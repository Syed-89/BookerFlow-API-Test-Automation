package com.framework.tests;

import com.framework.base.BaseTest;
import com.framework.data.BookingDataFactory;
import com.framework.models.booking.BookingCreated;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import org.testng.annotations.Test;

import static com.framework.assertions.BookingAssertions.assertMatchesSchema;
import static com.framework.base.TestGroups.CONTRACT;

@Epic("Restful Booker API")
@Feature("Contract")
public class BookingContractTest extends BaseTest {

    @Test(groups = CONTRACT, description = "GET /booking/{id} response matches the JSON schema")
    @Severity(SeverityLevel.NORMAL)
    public void bookingResponseMatchesSchema() {
        BookingCreated created = createBooking(BookingDataFactory.validBooking());

        assertMatchesSchema(bookingClient.getBooking(created.getBookingid()), "schemas/booking-schema.json");
    }
}
