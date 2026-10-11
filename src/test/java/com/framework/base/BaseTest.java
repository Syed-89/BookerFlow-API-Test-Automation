package com.framework.base;

import com.framework.auth.NoAuthStrategy;
import com.framework.client.ApiResponse;
import com.framework.client.BookingApiClient;
import com.framework.config.ConfigManager;
import com.framework.data.ResourceRegistry;
import com.framework.mock.LocalBookingApiServer;
import com.framework.models.booking.Booking;
import com.framework.models.booking.BookingCreated;
import com.framework.reporting.AllureEnvironmentWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeSuite;

import static com.framework.assertions.BookingAssertions.assertStatus;

/**
 * Parent of all test classes: suite lifecycle, shared (stateless => thread-safe) clients
 * and a "given a booking exists" helper so tests never depend on each other's data.
 */
public abstract class BaseTest {

    protected static final Logger LOG = LoggerFactory.getLogger(BaseTest.class);

    /** Authenticated with the mechanism configured in auth.type. */
    protected BookingApiClient bookingClient;
    /** Sends no credentials. */
    protected BookingApiClient anonymousClient;

    @BeforeSuite(alwaysRun = true)
    public void beforeSuite() {
        LocalBookingApiServer.startIfNeeded();
        AllureEnvironmentWriter.write();
        LOG.info("Running against {} ({})", ConfigManager.get().baseUrl(), ConfigManager.environment());
    }

    @BeforeClass(alwaysRun = true)
    public void initClients() {
        bookingClient = new BookingApiClient();
        anonymousClient = new BookingApiClient(new NoAuthStrategy());
    }

    @AfterSuite(alwaysRun = true)
    public void afterSuite() {
        try {
            if (!ConfigManager.get().cleanupEnabled()) {
                return;
            }
            BookingApiClient cleaner = new BookingApiClient();
            for (int id : ResourceRegistry.drain()) {
                try {
                    cleaner.deleteBooking(id);
                } catch (RuntimeException e) {
                    LOG.warn("Cleanup of booking {} failed: {}", id, e.getMessage());
                }
            }
        } finally {
            LocalBookingApiServer.stopIfNeeded();
        }
    }

    /** Precondition helper: creates a booking and fails fast if that does not work. */
    protected BookingCreated createBooking(Booking booking) {
        ApiResponse<BookingCreated> response = bookingClient.createBooking(booking);
        assertStatus(response, 200);
        return response.body();
    }
}
