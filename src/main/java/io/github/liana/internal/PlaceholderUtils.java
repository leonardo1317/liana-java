package io.github.liana.internal;

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

    public static <V> Optional<String> replaceIfAllPresent(String pattern, Map<String, V> variableMap) {
        if (pattern.isBlank()) {
            return Optional.empty();
        }

        Set<String> requiredPlaceholders = extractPlaceholders(pattern);
        if (requiredPlaceholders.isEmpty()) {
            return Optional.of(pattern);
        }

        boolean allPlaceholdersValid = requiredPlaceholders.stream()
                .allMatch(variableMap::containsKey);

        return allPlaceholdersValid ? Optional.of(replace(pattern, variableMap)) : Optional.empty();
    }


    public static <V> String replace(String pattern, Map<String, V> valueMap) {
        Matcher matcher = PATTERN.matcher(pattern);
        StringBuilder result = new StringBuilder();

        while (matcher.find()) {
            String key = matcher.group(1);
            Object replacement = valueMap.get(key);
            String replacementStr = replacement != null ? replacement.toString() : matcher.group(0);
            matcher.appendReplacement(result, Matcher.quoteReplacement(replacementStr));
        }
        matcher.appendTail(result);
        return result.toString();
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
