package io.github.liana.config;

import java.io.IOException;
import java.io.InputStream;

class XmlConfiguration extends AbstractJacksonConfiguration {

    public XmlConfiguration(InputStream in) throws IOException {
        super(ObjectMapperProvider.getXmlInstance(), in);
    }
}
