package com.framework.tests;

import com.framework.base.BaseTest;
import com.framework.client.ApiResponse;
import com.framework.data.BookingDataFactory;
import com.framework.models.booking.Booking;
import com.framework.models.booking.BookingCreated;
import com.framework.models.booking.BookingId;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import org.testng.annotations.Test;

import java.util.Map;

import static com.framework.assertions.BookingAssertions.assertStatus;
import static com.framework.base.TestGroups.REGRESSION;
import static org.assertj.core.api.Assertions.assertThat;

@Epic("Restful Booker API")
@Feature("Booking search")
public class BookingSearchTest extends BaseTest {

    @Test(groups = REGRESSION, description = "Filter by first and last name finds the booking")
    @Severity(SeverityLevel.NORMAL)
    public void shouldFindBookingByName() {
        Booking booking = BookingDataFactory.defaultBuilder()
                .firstname(BookingDataFactory.uniqueName("First"))
                .lastname(BookingDataFactory.uniqueName("Last"))
                .build();
        BookingCreated created = createBooking(booking);

        ApiResponse<BookingId[]> response = bookingClient.getBookingIds(
                Map.of("firstname", booking.getFirstname(), "lastname", booking.getLastname()));

        assertStatus(response, 200);
        assertThat(response.body()).extracting(BookingId::getBookingid).containsExactly(created.getBookingid());
    }

    @Test(groups = REGRESSION, description = "Filter with no match returns an empty list")
    @Severity(SeverityLevel.MINOR)
    public void shouldReturnEmptyListWhenNothingMatches() {
        ApiResponse<BookingId[]> response = bookingClient.getBookingIds(
                Map.of("firstname", BookingDataFactory.uniqueName("NoSuchPerson")));

        assertStatus(response, 200);
        assertThat(response.body()).isEmpty();
    }
}
