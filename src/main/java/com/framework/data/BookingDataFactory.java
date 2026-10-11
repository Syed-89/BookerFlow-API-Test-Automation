package com.framework.data;

import com.framework.models.booking.Booking;
import com.framework.models.booking.BookingDates;
import net.datafaker.Faker;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Programmatic test data: realistic, randomised, always valid unless a test overrides
 * a field. Returns builders so each test states only what it cares about, e.g.
 * {@code BookingDataFactory.defaultBuilder().totalprice(0).build()}.
 */
public final class BookingDataFactory {

    private static final ThreadLocal<Faker> FAKER = ThreadLocal.withInitial(Faker::new);

    private BookingDataFactory() {
    }

    public static Booking.BookingBuilder defaultBuilder() {
        Faker faker = FAKER.get();
        LocalDate checkin = LocalDate.now().plusDays(faker.number().numberBetween(1, 30));
        LocalDate checkout = checkin.plusDays(faker.number().numberBetween(1, 14));

        return Booking.builder()
                .firstname(faker.name().firstName())
                .lastname(faker.name().lastName())
                .totalprice(faker.number().numberBetween(100, 2000))
                .depositpaid(faker.bool().bool())
                .bookingdates(BookingDates.builder().checkin(checkin).checkout(checkout).build())
                .additionalneeds(faker.options().option("Breakfast", "Dinner", "Parking", "Late checkout"));
    }

    public static Booking validBooking() {
        return defaultBuilder().build();
    }

    /** A name that is practically guaranteed unique - for search tests on a shared server. */
    public static String uniqueName(String prefix) {
        return prefix + "-" + UUID.randomUUID().toString().substring(0, 8);
    }

    /** Payload for PATCH: only the fields being changed are populated. */
    public static Booking partialUpdate(String firstname, int totalPrice) {
        return Booking.builder().firstname(firstname).totalprice(totalPrice).build();
    }
}
