package io.github.liana.configs;

import org.apache.commons.configuration2.Configuration;

public interface ConfigLoader {
    Configuration load(String path);
}
