package io.github.liana.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.javaprop.JavaPropsMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

/**
 * Provides singleton instances of {@link ObjectMapper} and its variants (YAML, XML, Properties)
 * to ensure consistent configuration and efficient reuse across the application.
 */
public final class ObjectMapperProvider {
    private static final ObjectMapper JSON_MAPPER;
    private static final ObjectMapper YAML_MAPPER;
    private static final ObjectMapper XML_MAPPER;
    private static final ObjectMapper PROPERTIES_MAPPER;

    static {
        JSON_MAPPER = configureMapper(new ObjectMapper());
        YAML_MAPPER = configureMapper(new ObjectMapper(new YAMLFactory()));
        XML_MAPPER = configureMapper(new XmlMapper());
        PROPERTIES_MAPPER = configureMapper(new JavaPropsMapper());
    }

    private ObjectMapperProvider(){}

    private static <T extends ObjectMapper> T configureMapper(T mapper) {
        mapper.registerModule(new JavaTimeModule());
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return mapper;
    }

    /**
     * Returns the singleton JSON {@link ObjectMapper} instance.
     *
     * @return the shared JSON ObjectMapper instance
     */
    public static ObjectMapper getJsonInstance() {
        return JSON_MAPPER;
    }

    /**
     * Returns the singleton YAML {@link ObjectMapper} instance.
     *
     * @return the shared YAML ObjectMapper instance
     */
    public static ObjectMapper getYamlInstance() {
        return YAML_MAPPER;
    }

    /**
     * Returns the singleton XML {@link ObjectMapper} instance.
     *
     * @return the shared XML ObjectMapper instance
     */
    public static ObjectMapper getXmlInstance() {
        return XML_MAPPER;
    }

    /**
     * Returns the singleton Java Properties {@link ObjectMapper} instance.
     *
     * @return the shared Java Properties ObjectMapper instance
     */
    public static ObjectMapper getPropertiesInstance() {
        return PROPERTIES_MAPPER;
    }
}
