package com.framework.tests;

import com.framework.auth.TokenAuthStrategy;
import com.framework.base.BaseTest;
import com.framework.client.BookingApiClient;
import com.framework.data.BookingDataFactory;
import com.framework.models.booking.BookingCreated;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import org.testng.annotations.Test;

import static com.framework.assertions.BookingAssertions.assertStatus;
import static com.framework.base.TestGroups.NEGATIVE;
import static com.framework.base.TestGroups.SMOKE;

@Epic("Restful Booker API")
@Feature("Authorization")
public class BookingSecurityTest extends BaseTest {

    @Test(groups = NEGATIVE, description = "PUT without credentials is forbidden")
    @Severity(SeverityLevel.CRITICAL)
    public void updateWithoutAuthIsForbidden() {
        BookingCreated created = createBooking(BookingDataFactory.validBooking());

        assertStatus(anonymousClient.updateBooking(created.getBookingid(), BookingDataFactory.validBooking()), 403);
    }

    @Test(groups = NEGATIVE, description = "PATCH without credentials is forbidden")
    @Severity(SeverityLevel.CRITICAL)
    public void patchWithoutAuthIsForbidden() {
        BookingCreated created = createBooking(BookingDataFactory.validBooking());

        assertStatus(anonymousClient.partialUpdateBooking(created.getBookingid(),
                BookingDataFactory.partialUpdate("Hacker", 1)), 403);
    }

    @Test(groups = NEGATIVE, description = "DELETE without credentials is forbidden and booking survives")
    @Severity(SeverityLevel.CRITICAL)
    public void deleteWithoutAuthIsForbidden() {
        BookingCreated created = createBooking(BookingDataFactory.validBooking());

        assertStatus(anonymousClient.deleteBooking(created.getBookingid()), 403);
        assertStatus(bookingClient.getBooking(created.getBookingid()), 200);
    }

    @Test(groups = NEGATIVE, description = "A forged token is rejected")
    @Severity(SeverityLevel.CRITICAL)
    public void invalidTokenIsForbidden() {
        BookingCreated created = createBooking(BookingDataFactory.validBooking());
        BookingApiClient forged = new BookingApiClient(new TokenAuthStrategy(() -> "invalid-token"));

        assertStatus(forged.deleteBooking(created.getBookingid()), 403);
    }

    @Test(groups = SMOKE, description = "Reading a booking is public")
    @Severity(SeverityLevel.NORMAL)
    public void readingDoesNotRequireAuth() {
        BookingCreated created = createBooking(BookingDataFactory.validBooking());

        assertStatus(anonymousClient.getBooking(created.getBookingid()), 200);
    }
}
