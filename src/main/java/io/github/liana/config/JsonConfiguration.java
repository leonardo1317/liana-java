package io.github.liana.config;

import java.io.IOException;
import java.io.InputStream;

class JsonConfiguration extends AbstractJacksonConfiguration {

    public JsonConfiguration(InputStream in) throws IOException {
        super(ObjectMapperProvider.getJsonInstance(), in);
    }
}
