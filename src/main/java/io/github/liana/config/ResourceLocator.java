package io.github.liana.config;

import java.io.InputStream;

public interface ResourceLocator {

  boolean resourceExists(String resourceName);

  InputStream getResourceAsStream(String resourceName);
}
