package com.framework.data;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Tracks the ids of bookings created during the run so the suite can delete them
 * afterwards (keeps the shared environment clean, tests stay independent).
 */
public final class ResourceRegistry {

    private static final Set<Integer> BOOKING_IDS = ConcurrentHashMap.newKeySet();

    private ResourceRegistry() {
    }

    public static void register(int bookingId) {
        BOOKING_IDS.add(bookingId);
    }

    public static void deregister(int bookingId) {
        BOOKING_IDS.remove(bookingId);
    }

    /** Returns everything still registered and clears the registry. */
    public static Set<Integer> drain() {
        Set<Integer> snapshot = new HashSet<>(BOOKING_IDS);
        BOOKING_IDS.removeAll(snapshot);
        return snapshot;
    }
}
