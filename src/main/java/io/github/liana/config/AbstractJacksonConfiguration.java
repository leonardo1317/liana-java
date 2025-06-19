package io.github.liana.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import static java.util.Objects.requireNonNull;

abstract class AbstractJacksonConfiguration extends AbstractConfiguration {

    protected AbstractJacksonConfiguration(ObjectMapper mapper, InputStream in) throws IOException {
        super(read(mapper, in));
    }

    private static Map<String, Object> read(ObjectMapper mapper, InputStream in) throws IOException {
        requireNonNull(mapper, "ObjectMapper must not be null");
        requireNonNull(in, "InputStream must not be null");
        return mapper.readValue(in, new TypeReference<>() {});
    }
}
