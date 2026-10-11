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
import io.qameta.allure.Story;
import org.testng.annotations.Test;

import static com.framework.assertions.BookingAssertions.assertBookingEquals;
import static com.framework.assertions.BookingAssertions.assertStatus;
import static com.framework.base.TestGroups.REGRESSION;
import static com.framework.base.TestGroups.SMOKE;
import static org.assertj.core.api.Assertions.assertThat;

@Epic("Restful Booker API")
@Feature("Booking CRUD")
public class BookingCrudTest extends BaseTest {

    @Test(groups = SMOKE, description = "POST /booking creates a booking and echoes the payload")
    @Story("Create")
    @Severity(SeverityLevel.BLOCKER)
    public void shouldCreateBooking() {
        Booking request = BookingDataFactory.validBooking();

        ApiResponse<BookingCreated> response = bookingClient.createBooking(request);

        assertStatus(response, 200);
        assertThat(response.body().getBookingid()).isPositive();
        assertBookingEquals(response.body().getBooking(), request);
    }

    @Test(groups = SMOKE, description = "GET /booking/{id} returns the stored booking")
    @Story("Read")
    @Severity(SeverityLevel.BLOCKER)
    public void shouldGetBookingById() {
        Booking request = BookingDataFactory.validBooking();
        BookingCreated created = createBooking(request);

        ApiResponse<Booking> response = bookingClient.getBooking(created.getBookingid());

        assertStatus(response, 200);
        assertBookingEquals(response.body(), request);
    }

    @Test(groups = REGRESSION, description = "GET /booking lists the new booking id")
    @Story("Read")
    @Severity(SeverityLevel.NORMAL)
    public void shouldListCreatedBookingId() {
        BookingCreated created = createBooking(BookingDataFactory.validBooking());

        ApiResponse<BookingId[]> response = bookingClient.getBookingIds();

        assertStatus(response, 200);
        assertThat(response.body()).extracting(BookingId::getBookingid).contains(created.getBookingid());
    }

    @Test(groups = SMOKE, description = "PUT /booking/{id} replaces the booking")
    @Story("Update")
    @Severity(SeverityLevel.CRITICAL)
    public void shouldFullyUpdateBooking() {
        BookingCreated created = createBooking(BookingDataFactory.validBooking());
        Booking replacement = BookingDataFactory.validBooking();

        ApiResponse<Booking> response = bookingClient.updateBooking(created.getBookingid(), replacement);

        assertStatus(response, 200);
        assertBookingEquals(response.body(), replacement);
        assertBookingEquals(bookingClient.getBooking(created.getBookingid()).body(), replacement);
    }

    @Test(groups = REGRESSION, description = "PATCH /booking/{id} changes only the supplied fields")
    @Story("Update")
    @Severity(SeverityLevel.CRITICAL)
    public void shouldPartiallyUpdateBooking() {
        Booking original = BookingDataFactory.validBooking();
        BookingCreated created = createBooking(original);
        Booking patch = BookingDataFactory.partialUpdate("Patched", 4321);

        ApiResponse<Booking> response = bookingClient.partialUpdateBooking(created.getBookingid(), patch);

        assertStatus(response, 200);
        assertThat(response.body().getFirstname()).isEqualTo("Patched");
        assertThat(response.body().getTotalprice()).isEqualTo(4321);
        assertThat(response.body().getLastname()).isEqualTo(original.getLastname());
    }

    @Test(groups = SMOKE, description = "DELETE /booking/{id} removes the booking")
    @Story("Delete")
    @Severity(SeverityLevel.CRITICAL)
    public void shouldDeleteBooking() {
        BookingCreated created = createBooking(BookingDataFactory.validBooking());

        ApiResponse<String> deleted = bookingClient.deleteBooking(created.getBookingid());

        assertStatus(deleted, 201);
        assertStatus(bookingClient.getBooking(created.getBookingid()), 404);
    }
}
