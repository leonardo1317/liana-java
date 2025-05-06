package io.github.liana.config;

public final class StringUtils {
    public static final String DOT = ".";
    public static final String EMPTY_STRING = "";

    private StringUtils() {
    }

    public static String defaultIfBlank(String value, String defaultValue) {
        return value == null || value.isBlank() ? defaultValue : value;
    }
}
