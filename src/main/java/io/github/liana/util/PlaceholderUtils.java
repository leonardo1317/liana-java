package io.github.liana.util;

import org.apache.commons.text.StringSubstitutor;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class PlaceholderUtils {
    private static final String DEFAULT_PREFIX = "${";
    private static final String DEFAULT_SUFFIX = "}";
    private static final Pattern PATTERN = Pattern.compile(
            Pattern.quote(DEFAULT_PREFIX) + "(\\w+)" + Pattern.quote(DEFAULT_SUFFIX)
    );

    private PlaceholderUtils() {
    }

    public static <V> Optional<String> replaceIfAllPresent(String pattern, Map<String, V> valueMap) {
        if (pattern.isBlank()) {
            return Optional.empty();
        }

        Set<String> requiredPlaceholders = extractPlaceholders(pattern);
        if (requiredPlaceholders.isEmpty()) {
            return Optional.of(pattern);
        }

        boolean allPlaceholdersValid = requiredPlaceholders.stream()
                .allMatch(valueMap::containsKey);

        return allPlaceholdersValid ? Optional.of(replace(pattern, valueMap)) : Optional.empty();
    }

    public static <V> String replace(String pattern, Map<String, V> valueMap) {

        return StringSubstitutor.replace(pattern, valueMap, DEFAULT_PREFIX, DEFAULT_SUFFIX);
    }

    public static Set<String> extractPlaceholders(String pattern) {
        Set<String> placeholders = new LinkedHashSet<>();
        Matcher matcher = PATTERN.matcher(pattern);
        while (matcher.find()) {
            placeholders.add(matcher.group(1));
        }

        return placeholders;
    }
}
