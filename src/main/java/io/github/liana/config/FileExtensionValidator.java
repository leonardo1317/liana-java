package io.github.liana.config;

import java.util.Set;
import java.util.List;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.stream.Collectors;

final class FileExtensionValidator {
    private static final Set<String> DEFAULT_EXTENSIONS = Collections.unmodifiableSet(new LinkedHashSet<>(List.of("properties", "yaml", "yml", "json", "xml")));

    private FileExtensionValidator() {
    }

    public static boolean isValid(Set<String> allowedExtensions, String fileExtension) {
        if (allowedExtensions == null || allowedExtensions.isEmpty() ||
                fileExtension == null || fileExtension.isBlank()) {
            return false;
        }

        Set<String> extensions = allowedExtensions.stream()
                .filter(extension -> extension != null && !extension.isBlank())
                .map(String::trim)
                .map(String::toLowerCase)
                .collect(Collectors.toSet());

        return extensions.contains(fileExtension.trim().toLowerCase());
    }

    public static Set<String> defaultExtensions() {
        return DEFAULT_EXTENSIONS;
    }
}
