package com.framework.client;

import com.framework.auth.AuthStrategy;
import com.framework.auth.AuthStrategyFactory;
import com.framework.data.ResourceRegistry;
import com.framework.models.booking.Booking;
import com.framework.models.booking.BookingCreated;
import com.framework.models.booking.BookingId;
import io.qameta.allure.Step;
import io.restassured.response.Response;

import java.util.Map;

/**
 * Client for the /booking resource.
 * Default constructor uses the auth mechanism from configuration; the second constructor
 * lets a test choose any strategy (Basic, token, none, deliberately invalid...).
 * Bookings created through this client are registered for end-of-suite cleanup.
 */
public class BookingApiClient extends BaseApiClient {

    public BookingApiClient() {
        this(AuthStrategyFactory.fromConfig());
    }

    public BookingApiClient(AuthStrategy authStrategy) {
        super(authStrategy);
    }

    @Step("Get all booking ids")
    public ApiResponse<BookingId[]> getBookingIds() {
        return getBookingIds(Map.of());
    }

    @Step("Search bookings with filters {filters}")
    public ApiResponse<BookingId[]> getBookingIds(Map<String, String> filters) {
        return ApiResponse.of(get(Endpoints.BOOKING, filters), BookingId[].class);
    }

    @Step("Get booking {id}")
    public ApiResponse<Booking> getBooking(int id) {
        return ApiResponse.of(get(Endpoints.BOOKING_BY_ID, null, id), Booking.class);
    }

    @Step("Create booking")
    public ApiResponse<BookingCreated> createBooking(Booking booking) {
        Response raw = post(Endpoints.BOOKING, booking);
        if (raw.getStatusCode() == 200) {
            ResourceRegistry.register(raw.jsonPath().getInt("bookingid"));
        }
        return ApiResponse.of(raw, BookingCreated.class);
    }

    /** Sends an arbitrary payload (used by negative tests to violate the contract). */
    @Step("Create booking with raw payload")
    public ApiResponse<String> createBookingRaw(Object payload) {
        return ApiResponse.of(post(Endpoints.BOOKING, payload), String.class);
    }

    @Step("Update booking {id} (PUT)")
    public ApiResponse<Booking> updateBooking(int id, Booking booking) {
        return ApiResponse.of(put(Endpoints.BOOKING_BY_ID, booking, id), Booking.class);
    }

    @Step("Partially update booking {id} (PATCH)")
    public ApiResponse<Booking> partialUpdateBooking(int id, Booking partial) {
        return ApiResponse.of(patch(Endpoints.BOOKING_BY_ID, partial, id), Booking.class);
    }

    @Step("Delete booking {id}")
    public ApiResponse<String> deleteBooking(int id) {
        Response raw = delete(Endpoints.BOOKING_BY_ID, id);
        if (raw.getStatusCode() == 201) {
            ResourceRegistry.deregister(id);
        }
        return ApiResponse.of(raw, String.class);
    }
}
