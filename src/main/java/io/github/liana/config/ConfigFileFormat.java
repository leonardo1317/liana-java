package io.github.liana.config;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import static java.util.Objects.requireNonNull;

/**
 * Supported configuration file formats and their associated extensions.
 */
public enum ConfigFileFormat {
    PROPERTIES(List.of("properties")),
    YAML(List.of("yaml", "yml")),
    JSON(List.of("json")),
    XML(List.of("xml"));

    private final List<String> extensions;
    private static final List<String> ALL_EXTENSIONS;

    static {
        Set<String> tempExtensions = new LinkedHashSet<>();
        for (ConfigFileFormat fileFormat : values()) {
            for (String extension : fileFormat.extensions) {
                if (!tempExtensions.add(extension.toLowerCase(Locale.ROOT))) {
                    throw new IllegalStateException("duplicate extension: " + extension);
                }
            }
        }

        ALL_EXTENSIONS = List.copyOf(tempExtensions);
    }

    ConfigFileFormat(List<String> extensions) {
        this.extensions = List.copyOf(extensions);
    }

    /**
     * Gets all valid extensions for this format.
     *
     * @return Immutable list of extensions in lowercase
     */
    public List<String> getExtensions() {
        return extensions;
    }

    /**
     * Gets all supported extensions across all formats (no duplicates).
     *
     * @return Immutable list in declaration order
     */
    public static List<String> getAllSupportedExtensions() {
        return ALL_EXTENSIONS;
    }

    /**
     * Checks if an extension belongs to a specific format.
     *
     * @param format    Format to check (non-null)
     * @param extension Extension to verify (non-null)
     * @return true if the extension is valid for the specified format
     * @throws NullPointerException if format or extension is null
     */
    public static boolean isExtensionForFormat(ConfigFileFormat format, String extension) {
        requireNonNull(format, "Format cannot be null");
        requireNonNull(extension, "Extension cannot be null");
        return format.getExtensions().contains(extension.toLowerCase(Locale.ROOT));
    }
}
