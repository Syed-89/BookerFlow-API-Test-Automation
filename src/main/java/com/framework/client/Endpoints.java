package com.framework.client;

/** Central catalogue of API paths - the only place a URL fragment is written. */
public final class Endpoints {

    public static final String AUTH = "/auth";
    public static final String PING = "/ping";
    public static final String BOOKING = "/booking";
    public static final String BOOKING_BY_ID = "/booking/{id}";

    private Endpoints() {
    }
}
