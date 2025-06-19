package io.github.liana.internal;

public final class StringUtils {

    private StringUtils() {
    }

    public static String defaultIfBlank(String value, String defaultValue) {
        return isBlank(value) ? defaultValue : value;
    }

    public static String requireNonBlank(String value, String message) {
        if (isBlank(value)) {
            throw new IllegalArgumentException(message);
        }

        return value;
    }

    public static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    public static boolean equalsIgnoreCase(String first, String second) {
        return first == null ? second == null : first.equalsIgnoreCase(second);
    }
}
