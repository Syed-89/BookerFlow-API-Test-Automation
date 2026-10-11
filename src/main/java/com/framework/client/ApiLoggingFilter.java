package com.framework.client;

import io.restassured.filter.Filter;
import io.restassured.filter.FilterContext;
import io.restassured.http.Header;
import io.restassured.response.Response;
import io.restassured.specification.FilterableRequestSpecification;
import io.restassured.specification.FilterableResponseSpecification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;
import java.util.StringJoiner;

/**
 * SLF4J request/response logger. INFO = one line per call, DEBUG = headers and bodies.
 * Credentials never reach the log: sensitive headers are masked and /auth bodies redacted.
 */
public class ApiLoggingFilter implements Filter {

    private static final Logger LOG = LoggerFactory.getLogger(ApiLoggingFilter.class);
    private static final Set<String> SENSITIVE_HEADERS = Set.of("authorization", "cookie", "set-cookie");

    @Override
    public Response filter(FilterableRequestSpecification request,
                           FilterableResponseSpecification responseSpec,
                           FilterContext ctx) {
        boolean authCall = request.getURI().endsWith(Endpoints.AUTH);
        LOG.info("--> {} {}", request.getMethod(), request.getURI());
        if (LOG.isDebugEnabled()) {
            Object body = request.getBody();
            LOG.debug("    headers: {}", maskedHeaders(request));
            LOG.debug("    body: {}", authCall ? "[REDACTED]" : body);
        }

        long start = System.nanoTime();
        Response response = ctx.next(request, responseSpec);
        long elapsedMs = (System.nanoTime() - start) / 1_000_000;

        LOG.info("<-- {} {} {} ({} ms)", response.getStatusCode(), request.getMethod(), request.getURI(), elapsedMs);
        if (LOG.isDebugEnabled()) {
            LOG.debug("    body: {}", authCall ? "[REDACTED]" : response.asString());
        }
        return response;
    }

    private static String maskedHeaders(FilterableRequestSpecification request) {
        StringJoiner joiner = new StringJoiner(", ", "[", "]");
        for (Header header : request.getHeaders()) {
            String value = SENSITIVE_HEADERS.contains(header.getName().toLowerCase()) ? "****" : header.getValue();
            joiner.add(header.getName() + "=" + value);
        }
        return joiner.toString();
    }
}
