package com.framework.data;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.framework.exceptions.FrameworkException;
import com.framework.serialization.ObjectMapperFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/** Loads static JSON test data from the classpath into typed objects. */
public final class JsonDataReader {

    private JsonDataReader() {
    }

    public static <T> List<T> readList(String classpathResource, Class<T> elementType) {
        ObjectMapper mapper = ObjectMapperFactory.getInstance();
        try (InputStream in = open(classpathResource)) {
            return mapper.readValue(in, mapper.getTypeFactory().constructCollectionType(List.class, elementType));
        } catch (IOException e) {
            throw new FrameworkException("Cannot parse test data " + classpathResource, e);
        }
    }

    public static <T> T readObject(String classpathResource, Class<T> type) {
        try (InputStream in = open(classpathResource)) {
            return ObjectMapperFactory.getInstance().readValue(in, type);
        } catch (IOException e) {
            throw new FrameworkException("Cannot parse test data " + classpathResource, e);
        }
    }

    private static InputStream open(String resource) {
        InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream(resource);
        if (in == null) {
            throw new FrameworkException("Test data not found on classpath: " + resource);
        }
        return in;
    }
}
