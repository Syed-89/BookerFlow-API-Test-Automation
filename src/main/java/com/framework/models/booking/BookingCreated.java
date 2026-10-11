package com.framework.models.booking;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Objects;

/** Response body of POST /booking. */
@JsonIgnoreProperties(ignoreUnknown = true)
public class BookingCreated {
    private Integer bookingid;
    private Booking booking;

    public BookingCreated() {
    }

    public BookingCreated(Integer bookingid, Booking booking) {
        this.bookingid = bookingid;
        this.booking = booking;
    }

    public static Builder builder() {
        return new Builder();
    }

    public Integer getBookingid() {
        return bookingid;
    }

    public void setBookingid(Integer bookingid) {
        this.bookingid = bookingid;
    }

    public Booking getBooking() {
        return booking;
    }

    public void setBooking(Booking booking) {
        this.booking = booking;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BookingCreated that = (BookingCreated) o;
        return Objects.equals(bookingid, that.bookingid) && Objects.equals(booking, that.booking);
    }

    @Override
    public int hashCode() {
        return Objects.hash(bookingid, booking);
    }

    @Override
    public String toString() {
        return "BookingCreated{bookingid=" + bookingid + ", booking=" + booking + "}";
    }

    public static final class Builder {
        private Integer bookingid;
        private Booking booking;

        public Builder bookingid(Integer bookingid) {
            this.bookingid = bookingid;
            return this;
        }

        public Builder booking(Booking booking) {
            this.booking = booking;
            return this;
        }

        public BookingCreated build() {
            return new BookingCreated(bookingid, booking);
        }
    }
}
