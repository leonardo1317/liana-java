package io.github.liana.internal;

import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility class for resolving placeholders in strings using a variable map.
 *
 * <p>Supports placeholders in the format {@code ${key}} and {@code ${key:default}}. Placeholders
 * are resolved by substituting them with the corresponding value from the provided map, or a
 * default value if specified.
 *
 * <p>This class is stateless and cannot be instantiated.
 */
public final class PlaceholderUtils {

  private static final String DEFAULT_PREFIX = "${";
  private static final String DEFAULT_SUFFIX = "}";

  private static final Pattern PATTERN = Pattern.compile(
      Pattern.quote(DEFAULT_PREFIX) + "(\\w+)(?::([^}]+))?" + Pattern.quote(DEFAULT_SUFFIX)
  );

  private PlaceholderUtils() {
  }

  /**
   * Replaces all placeholders in the given pattern only if all placeholders can be resolved using
   * the provided value map or have default values defined.
   *
   * <p>A placeholder is considered resolvable if:
   * <ul>
   *   <li>The key exists in {@code valueMap}, or</li>
   *   <li>A default value is specified for that placeholder.</li>
   * </ul>
   *
   * @param pattern  the input string containing placeholders (e.g., {@code "config-${env}.json"})
   * @param valueMap a map of variable names and their values
   * @param <V>      the type of values in the map, which will be converted to strings via
   *                 {@code toString()}
   * @return an {@code Optional} containing the resolved string if all placeholders are resolvable;
   * otherwise, {@code Optional.empty()}
   */
  public static <V> Optional<String> replaceIfAllResolvable(String pattern,
      Map<String, V> valueMap) {
    if (pattern == null || pattern.isBlank()) {
      return Optional.empty();
    }

    boolean allResolvable = PATTERN.matcher(pattern)
        .results()
        .allMatch(matcher -> {
          String key = matcher.group(1);
          String defaultValue = matcher.group(2);
          return valueMap.containsKey(key) || defaultValue != null;
        });

    return allResolvable ? Optional.of(replace(pattern, valueMap)) : Optional.empty();
  }

  /**
   * Replaces all placeholders in the given pattern using the provided value map.
   *
   * <p>If a placeholder's key exists in the map, its value will be used. If it is not found
   * in the map but a default value is specified, the default will be used. If neither is available,
   * the placeholder is left unchanged.
   *
   * @param pattern  the input string containing placeholders (e.g.,
   *                 {@code "file-${region:us}.json"})
   * @param valueMap a map of variable names and their values
   * @param <V>      the type of values in the map, which will be converted to strings via
   *                 {@code toString()}
   * @return the input string with all resolvable placeholders replaced
   */
  public static <V> String replace(String pattern, Map<String, V> valueMap) {
    Matcher matcher = PATTERN.matcher(pattern);
    StringBuilder result = new StringBuilder();

    while (matcher.find()) {
      String key = matcher.group(1);
      Object replacement = valueMap.get(key);
      String replacementStr =
          replacement != null ? replacement.toString() : getDefaultValue(matcher);
      matcher.appendReplacement(result, Matcher.quoteReplacement(replacementStr));
    }
    matcher.appendTail(result);
    return result.toString();
  }

  /**
   * Returns the default value for a placeholder if one is defined; otherwise returns the full
   * placeholder text.
   *
   * @param matcher the matcher currently evaluating a placeholder
   * @return the default value if specified; otherwise, the original placeholder text
   */
  private static String getDefaultValue(Matcher matcher) {
    return Optional.ofNullable(matcher.group(2)).orElse(matcher.group(0));
  }
}
