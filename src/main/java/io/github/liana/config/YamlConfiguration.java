package io.github.liana.config;

import java.io.IOException;
import java.io.InputStream;

class YamlConfiguration extends AbstractJacksonConfiguration {

    public YamlConfiguration(InputStream in) throws IOException {
        super(ObjectMapperProvider.getYamlInstance(), in);
    }
}
