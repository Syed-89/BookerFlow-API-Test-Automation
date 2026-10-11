package com.framework.models.booking;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Objects;

/** Element of the GET /booking response array. */
@JsonIgnoreProperties(ignoreUnknown = true)
public class BookingId {
    private Integer bookingid;

    public BookingId() {
    }

    public BookingId(Integer bookingid) {
        this.bookingid = bookingid;
    }

    public Integer getBookingid() {
        return bookingid;
    }

    public void setBookingid(Integer bookingid) {
        this.bookingid = bookingid;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BookingId bookingId = (BookingId) o;
        return Objects.equals(bookingid, bookingId.bookingid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(bookingid);
    }

    @Override
    public String toString() {
        return "BookingId{bookingid=" + bookingid + "}";
    }
}
