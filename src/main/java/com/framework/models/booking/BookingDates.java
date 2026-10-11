package com.framework.models.booking;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.LocalDate;
import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BookingDates {
    private LocalDate checkin;
    private LocalDate checkout;

    public BookingDates() {
    }

    public BookingDates(LocalDate checkin, LocalDate checkout) {
        this.checkin = checkin;
        this.checkout = checkout;
    }

    public static BookingDatesBuilder builder() {
        return new BookingDatesBuilder();
    }

    public BookingDates toBuilder() {
        return new BookingDates(checkin, checkout);
    }

    public LocalDate getCheckin() {
        return checkin;
    }

    public void setCheckin(LocalDate checkin) {
        this.checkin = checkin;
    }

    public LocalDate getCheckout() {
        return checkout;
    }

    public void setCheckout(LocalDate checkout) {
        this.checkout = checkout;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BookingDates that = (BookingDates) o;
        return Objects.equals(checkin, that.checkin) && Objects.equals(checkout, that.checkout);
    }

    @Override
    public int hashCode() {
        return Objects.hash(checkin, checkout);
    }

    @Override
    public String toString() {
        return "BookingDates{checkin=" + checkin + ", checkout=" + checkout + "}";
    }

    public static final class BookingDatesBuilder {
        private LocalDate checkin;
        private LocalDate checkout;

        public BookingDatesBuilder checkin(LocalDate checkin) {
            this.checkin = checkin;
            return this;
        }

        public BookingDatesBuilder checkout(LocalDate checkout) {
            this.checkout = checkout;
            return this;
        }

        public BookingDates build() {
            return new BookingDates(checkin, checkout);
        }
    }
}
