package com.framework.dataproviders;

import com.framework.data.JsonDataReader;
import com.framework.models.booking.Booking;
import org.testng.annotations.DataProvider;

/** TestNG data providers backed by JSON files in src/test/resources/testdata. */
public final class BookingDataProviders {

    private BookingDataProviders() {
    }

    @DataProvider(name = "validBookings", parallel = true)
    public static Object[][] validBookings() {
        return JsonDataReader.readList("testdata/valid_bookings.json", Booking.class).stream()
                .map(booking -> new Object[]{booking})
                .toArray(Object[][]::new);
    }

    @DataProvider(name = "invalidBookings", parallel = true)
    public static Object[][] invalidBookings() {
        return JsonDataReader.readList("testdata/invalid_bookings.json", InvalidBookingScenario.class).stream()
                .map(scenario -> new Object[]{scenario})
                .toArray(Object[][]::new);
    }
}
