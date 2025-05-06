package io.github.liana.config;

import java.io.InputStream;
import java.util.List;
import java.util.Set;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Objects;

final class ClasspathResource {
    private static final Set<String> DEFAULT_SEARCH_PATHS = Collections.unmodifiableSet(new LinkedHashSet<>(List.of("", "config")));

    public static boolean resourceExists(String resourceName) {

        if (resourceName == null || resourceName.isBlank()) {
            return false;
        }

        return DEFAULT_SEARCH_PATHS.stream()
                .map(path -> buildPath(path, resourceName))
                .anyMatch(resourcePath -> getClassLoader().getResource(resourcePath) != null);
    }

    public static InputStream getResourceAsStream(String resourceName) {
        if (resourceName == null || resourceName.isBlank()) {
            return null;
        }

        return DEFAULT_SEARCH_PATHS.stream()
                .map(path -> buildPath(path, resourceName))
                .map(resourcePath -> getClassLoader().getResourceAsStream(resourcePath))
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);
    }

    private static String buildPath(String path, String resourceName) {
        return path.isBlank() ? resourceName : path + "/" + resourceName;
    }

    private static ClassLoader getClassLoader() {
        return ClasspathResource.class.getClassLoader();
    }
}
