package com.framework.dataproviders;

import java.util.Map;
import java.util.Objects;

/** One negative scenario loaded from testdata/invalid_bookings.json. */
public class InvalidBookingScenario {
    private String name;
    private Map<String, Object> payload;
    private int expectedStatus;

    public InvalidBookingScenario() {
    }

    public InvalidBookingScenario(String name, Map<String, Object> payload, int expectedStatus) {
        this.name = name;
        this.payload = payload;
        this.expectedStatus = expectedStatus;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, Object> getPayload() {
        return payload;
    }

    public void setPayload(Map<String, Object> payload) {
        this.payload = payload;
    }

    public int getExpectedStatus() {
        return expectedStatus;
    }

    public void setExpectedStatus(int expectedStatus) {
        this.expectedStatus = expectedStatus;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InvalidBookingScenario that = (InvalidBookingScenario) o;
        return expectedStatus == that.expectedStatus
                && Objects.equals(name, that.name)
                && Objects.equals(payload, that.payload);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, payload, expectedStatus);
    }

    @Override
    public String toString() {
        return name;
    }
}
