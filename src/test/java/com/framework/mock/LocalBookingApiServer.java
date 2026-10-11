package com.framework.mock;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.framework.config.ConfigManager;
import com.framework.serialization.ObjectMapperFactory;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Self-contained in-memory mock of the Restful Booker API used to keep the tests stable.
 * It covers the endpoints and behaviors exercised by the suite without relying on the public Heroku sandbox.
 */
public final class LocalBookingApiServer {

    private static final ObjectMapper MAPPER = ObjectMapperFactory.getInstance();
    private static volatile LocalBookingApiServer instance;
    private static volatile HttpServer server;

    private final Map<Integer, Map<String, Object>> store = new ConcurrentHashMap<>();
    private final AtomicInteger idSequence = new AtomicInteger(1);
    private final String validUsername;
    private final String validPassword;
    private final String validToken;

    private LocalBookingApiServer() {
        this.validUsername = ConfigManager.get().authUsername();
        this.validPassword = ConfigManager.get().authPassword();
        this.validToken = "mock-token";
    }

    public static void startIfNeeded() {
        if (instance != null) {
            return;
        }
        instance = new LocalBookingApiServer();
        try {
            server = HttpServer.create(new InetSocketAddress(8080), 0);
            server.createContext("/", instance.new BookingHandler());
            server.setExecutor(null);
            server.start();
        } catch (IOException e) {
            throw new IllegalStateException("Unable to start mock booking API server", e);
        }
    }

    public static void stopIfNeeded() {
        if (server != null) {
            server.stop(0);
            server = null;
        }
        if (instance != null) {
            instance.store.clear();
            instance.idSequence.set(1);
            instance = null;
        }
    }

    private final class BookingHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String method = exchange.getRequestMethod();
            String path = exchange.getRequestURI().getPath();
            String query = exchange.getRequestURI().getRawQuery();

            try {
                if ("GET".equals(method) && "/ping".equals(path)) {
                    sendText(exchange, 201, "Created");
                    return;
                }

                if ("POST".equals(method) && "/auth".equals(path)) {
                    handleAuth(exchange);
                    return;
                }

                if ("GET".equals(method) && "/booking".equals(path)) {
                    handleListBookings(exchange, query);
                    return;
                }

                if ("POST".equals(method) && "/booking".equals(path)) {
                    handleCreateBooking(exchange);
                    return;
                }

                if (path.startsWith("/booking/")) {
                    String idPart = path.substring("/booking/".length());
                    int id = Integer.parseInt(idPart);
                    if ("GET".equals(method)) {
                        handleGetBooking(exchange, id);
                        return;
                    }
                    if ("PUT".equals(method)) {
                        handlePutBooking(exchange, id);
                        return;
                    }
                    if ("PATCH".equals(method)) {
                        handlePatchBooking(exchange, id);
                        return;
                    }
                    if ("DELETE".equals(method)) {
                        handleDeleteBooking(exchange, id);
                        return;
                    }
                }

                sendJson(exchange, 404, Collections.singletonMap("error", "Not Found"));
            } catch (NumberFormatException e) {
                sendJson(exchange, 400, Collections.singletonMap("error", "Invalid booking id"));
            } catch (Exception e) {
                sendJson(exchange, 500, Collections.singletonMap("error", e.getMessage()));
            }
        }
    }

    private void handleAuth(HttpExchange exchange) throws IOException {
        Map<String, Object> request = readJson(exchange);
        String username = asString(request.get("username"));
        String password = asString(request.get("password"));

        if (validUsername.equals(username) && validPassword.equals(password)) {
            sendJson(exchange, 200, Map.of("token", validToken));
            return;
        }

        sendJson(exchange, 200, Map.of("reason", "Bad credentials"));
    }

    private void handleListBookings(HttpExchange exchange, String query) throws IOException {
        Map<String, String> filters = parseQuery(query);
        List<Map<String, Object>> ids = new ArrayList<>();

        for (Map.Entry<Integer, Map<String, Object>> entry : store.entrySet()) {
            Map<String, Object> booking = entry.getValue();
            if (matchesFilters(booking, filters)) {
                Map<String, Object> item = new HashMap<>();
                item.put("bookingid", entry.getKey());
                ids.add(item);
            }
        }

        sendJson(exchange, 200, ids);
    }

    private void handleCreateBooking(HttpExchange exchange) throws IOException {
        Map<String, Object> request = readJson(exchange);
        if (!isValidBooking(request)) {
            sendJson(exchange, 500, Collections.singletonMap("error", "Invalid booking payload"));
            return;
        }

        int id = idSequence.getAndIncrement();
        store.put(id, normalizeBooking(request));
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("bookingid", id);
        response.put("booking", store.get(id));
        sendJson(exchange, 200, response);
    }

    private void handleGetBooking(HttpExchange exchange, int id) throws IOException {
        Map<String, Object> booking = store.get(id);
        if (booking == null) {
            sendJson(exchange, 404, Collections.singletonMap("error", "Not Found"));
            return;
        }
        sendJson(exchange, 200, booking);
    }

    private void handlePutBooking(HttpExchange exchange, int id) throws IOException {
        if (!hasValidAuth(exchange)) {
            sendJson(exchange, 403, Collections.singletonMap("error", "Forbidden"));
            return;
        }
        Map<String, Object> request = readJson(exchange);
        if (!isValidBooking(request)) {
            sendJson(exchange, 500, Collections.singletonMap("error", "Invalid booking payload"));
            return;
        }
        if (!store.containsKey(id)) {
            sendJson(exchange, 404, Collections.singletonMap("error", "Not Found"));
            return;
        }
        store.put(id, normalizeBooking(request));
        sendJson(exchange, 200, store.get(id));
    }

    private void handlePatchBooking(HttpExchange exchange, int id) throws IOException {
        if (!hasValidAuth(exchange)) {
            sendJson(exchange, 403, Collections.singletonMap("error", "Forbidden"));
            return;
        }
        Map<String, Object> request = readJson(exchange);
        if (!store.containsKey(id)) {
            sendJson(exchange, 404, Collections.singletonMap("error", "Not Found"));
            return;
        }
        Map<String, Object> original = store.get(id);
        Map<String, Object> merged = new LinkedHashMap<>(original);
        for (Map.Entry<String, Object> entry : request.entrySet()) {
            if (entry.getKey().equals("bookingdates") && entry.getValue() instanceof Map) {
                Map<String, Object> dates = (Map<String, Object>) entry.getValue();
                Map<String, Object> currentDates = (Map<String, Object>) merged.getOrDefault("bookingdates", new LinkedHashMap<String, Object>());
                currentDates.putAll(dates);
                merged.put("bookingdates", currentDates);
            } else {
                merged.put(entry.getKey(), entry.getValue());
            }
        }
        if (!isValidBooking(merged)) {
            sendJson(exchange, 500, Collections.singletonMap("error", "Invalid booking payload"));
            return;
        }
        store.put(id, normalizeBooking(merged));
        sendJson(exchange, 200, store.get(id));
    }

    private void handleDeleteBooking(HttpExchange exchange, int id) throws IOException {
        if (!hasValidAuth(exchange)) {
            sendJson(exchange, 403, Collections.singletonMap("error", "Forbidden"));
            return;
        }
        if (!store.containsKey(id)) {
            sendJson(exchange, 404, Collections.singletonMap("error", "Not Found"));
            return;
        }
        store.remove(id);
        sendText(exchange, 201, "Created");
    }

    private boolean hasValidAuth(HttpExchange exchange) {
        Headers headers = exchange.getRequestHeaders();

        String cookie = headers.getFirst("Cookie");
        if (cookie != null && cookie.contains("token=" + validToken)) {
            return true;
        }

        String authorization = headers.getFirst("Authorization");
        if (authorization != null && authorization.startsWith("Basic ")) {
            String encoded = authorization.substring("Basic ".length());
            String decoded = new String(Base64.getDecoder().decode(encoded), StandardCharsets.UTF_8);
            String[] parts = decoded.split(":", 2);
            if (parts.length == 2) {
                return validUsername.equals(parts[0]) && validPassword.equals(parts[1]);
            }
        }

        return false;
    }

    private Map<String, String> parseQuery(String query) {
        if (query == null || query.isBlank()) {
            return Collections.emptyMap();
        }
        Map<String, String> values = new HashMap<>();
        for (String part : query.split("&")) {
            String[] kv = part.split("=", 2);
            if (kv.length == 2) {
                values.put(kv[0], kv[1]);
            }
        }
        return values;
    }

    private boolean matchesFilters(Map<String, Object> booking, Map<String, String> filters) {
        for (Map.Entry<String, String> entry : filters.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            Object actual = booking.get(key);
            if (actual == null || !actual.toString().equalsIgnoreCase(value)) {
                return false;
            }
        }
        return true;
    }

    private boolean isValidBooking(Map<String, Object> booking) {
        if (booking == null || booking.isEmpty()) {
            return false;
        }
        if (asString(booking.get("firstname")).isBlank()) {
            return false;
        }
        if (booking.get("bookingdates") == null) {
            return false;
        }
        Map<String, Object> dates = asMap(booking.get("bookingdates"));
        if (dates == null || asString(dates.get("checkin")).isBlank() || asString(dates.get("checkout")).isBlank()) {
            return false;
        }
        return true;
    }

    private Map<String, Object> normalizeBooking(Map<String, Object> input) {
        Map<String, Object> booking = new LinkedHashMap<>();
        booking.put("firstname", asString(input.get("firstname")));
        booking.put("lastname", asString(input.get("lastname")));
        booking.put("totalprice", input.get("totalprice") == null ? null : Integer.valueOf(String.valueOf(input.get("totalprice"))));
        booking.put("depositpaid", input.get("depositpaid"));
        Object dates = input.get("bookingdates");
        if (dates instanceof Map) {
            booking.put("bookingdates", normalizeDates((Map<String, Object>) dates));
        }
        booking.put("additionalneeds", input.get("additionalneeds"));
        return booking;
    }

    private Map<String, Object> normalizeDates(Map<String, Object> input) {
        Map<String, Object> dates = new LinkedHashMap<>();
        dates.put("checkin", input.get("checkin"));
        dates.put("checkout", input.get("checkout"));
        return dates;
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> asMap(Object value) {
        if (value instanceof Map) {
            return (Map<String, Object>) value;
        }
        return null;
    }

    private String asString(Object value) {
        return value == null ? "" : String.valueOf(value);
    }

    private Map<String, Object> readJson(HttpExchange exchange) throws IOException {
        byte[] body = exchange.getRequestBody().readAllBytes();
        if (body == null || body.length == 0) {
            return Collections.emptyMap();
        }
        return MAPPER.readValue(body, Map.class);
    }

    private void sendJson(HttpExchange exchange, int status, Object payload) throws IOException {
        byte[] response = MAPPER.writeValueAsBytes(payload);
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        exchange.sendResponseHeaders(status, response.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response);
        }
    }

    private void sendText(HttpExchange exchange, int status, String payload) throws IOException {
        byte[] response = payload.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "text/plain; charset=UTF-8");
        exchange.sendResponseHeaders(status, response.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response);
        }
    }
}
