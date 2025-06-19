package io.github.liana.config;

import java.io.IOException;
import java.io.InputStream;

class PropertiesConfiguration extends AbstractJacksonConfiguration {

    public PropertiesConfiguration(InputStream in) throws IOException {
        super(ObjectMapperProvider.getPropertiesInstance(), in);
    }
}
