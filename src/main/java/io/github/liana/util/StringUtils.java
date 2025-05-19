package io.github.liana.util;

public final class StringUtils {
    public static final char DOT = '.';
    public static final String EMPTY_STRING = "";

    private StringUtils() {
    }

    public static String defaultIfBlank(String value, String defaultValue) {
        return value == null || value.isBlank() ? defaultValue : value;
    }

    public static boolean isBlank(String str) {
        return str == null || str.isBlank();
    }
}
