package io.github.liana.config.core;

import static io.github.liana.config.internal.StringUtils.isBlank;
import static io.github.liana.config.internal.StringUtils.requireNonBlank;
import static java.util.Objects.requireNonNull;

import io.github.liana.config.api.Placeholder;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;

/**
 * Default implementation of {@link Placeholder} supporting nested placeholders, fallback values,
 * and escaped prefix sequences.
 *
 * <p>This resolver is immutable and thread-safe. Placeholder syntax is fully configurable through
 * prefix, suffix, delimiter, and escape character parameters. Resolution is performed against a
 * predefined list of {@link PropertySource} instances, optionally extended at call time.
 *
 * <p>Resolution follows an "all-or-nothing" strategy: if any placeholder cannot be resolved using
 * the provided sources or its fallback value, the operation returns {@code Optional.empty()}.
 *
 * <p>This class is part of the internal implementation surface; consumers typically obtain
 * instances via {@code PlaceholderBuilder}.
 */
public final class DefaultPlaceholder implements Placeholder {

  private final List<PropertySource> sources;
  private final String prefix;
  private final String suffix;
  private final String delimiter;
  private final char escapeChar;

  /**
   * Creates a resolver using the system environment as the default property source.
   *
   * @param prefix     the placeholder prefix; must not be null or blank
   * @param suffix     the placeholder suffix; must not be null or blank
   * @param delimiter  the delimiter separating key and fallback value; must not be null or blank
   * @param escapeChar the character used to escape a prefix occurrence
   */
  public DefaultPlaceholder(String prefix, String suffix, String delimiter, char escapeChar) {
    this(prefix, suffix, delimiter, escapeChar, List.of(PropertySources.fromEnv()));
  }

  /**
   * Creates a resolver with explicit property sources.
   *
   * @param prefix     the placeholder prefix; must not be null or blank
   * @param suffix     the placeholder suffix; must not be null or blank
   * @param delimiter  the delimiter separating key and fallback value; must not be null or blank
   * @param escapeChar the character used to escape a prefix occurrence
   * @param sources    ordered property sources used to resolve placeholders; must not be null
   */
  public DefaultPlaceholder(String prefix, String suffix, String delimiter, char escapeChar,
      List<PropertySource> sources) {
    this.prefix = requireNonBlank(prefix, "prefix must not be null or blank");
    this.suffix = requireNonBlank(suffix, "suffix must not be null or blank");
    this.delimiter = requireNonBlank(delimiter, "delimiter must not be null or blank");
    this.escapeChar = escapeChar;
    this.sources = List.copyOf(requireNonNull(sources, "sources must not be null"));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Optional<String> replaceIfAllResolvable(String template, PropertySource... extraSources) {
    requireNonNull(template, "template must not be null");

    if (isBlank(template) || !template.contains(prefix)) {
      return Optional.of(template);
    }

    List<PropertySource> mergedSources = mergeSources(extraSources);
    if (mergedSources.isEmpty()) {
      return Optional.empty();
    }

    var unresolved = new HashSet<String>();
    String resolved = replace(template, mergedSources, new HashSet<>(), unresolved);
    return unresolved.isEmpty() ? Optional.of(resolved) : Optional.empty();
  }

  private List<PropertySource> mergeSources(PropertySource... extraSources) {
    List<PropertySource> merged = new ArrayList<>(this.sources);
    if (extraSources != null && extraSources.length > 0) {
      merged.addAll(List.of(extraSources));
    }
    return List.copyOf(merged);
  }

  private String replace(String template, List<PropertySource> sources,
      Set<String> keysInResolution, Set<String> unresolved) {
    var result = new StringBuilder();
    var stack = new ArrayDeque<Integer>();

    var i = 0;
    final var prefixAdvance = prefix.length();
    while (i < template.length()) {
      if (isEscapedPrefix(template, i)) {
        result.deleteCharAt(result.length() - 1);
        result.append(prefix);
        i += prefixAdvance;
      } else if (isStartOfPlaceholder(template, i)) {
        stack.push(result.length());
        result.append(prefix);
        i += prefixAdvance;
      } else if (isEndOfPlaceholder(template, i, stack)) {
        processPlaceholder(result, stack.pop(), sources, keysInResolution, unresolved);
        i += suffix.length();
      } else {
        result.append(template.charAt(i));
        i++;
      }
    }
    return result.toString();
  }

  private boolean isEscapedPrefix(String template, int index) {
    return index > 0 && template.startsWith(prefix, index)
        && template.charAt(index - 1) == escapeChar;
  }

  private boolean isStartOfPlaceholder(String template, int index) {
    return template.startsWith(prefix, index);
  }

  private boolean isEndOfPlaceholder(String template, int index, Deque<Integer> stack) {
    return template.startsWith(suffix, index) && !stack.isEmpty();
  }

  private void processPlaceholder(StringBuilder result, int startIndex,
      List<PropertySource> sources,
      Set<String> keysInResolution, Set<String> unresolved) {
    String placeholder = result.substring(startIndex + prefix.length());
    result.delete(startIndex, result.length());
    String resolved = resolvePlaceholder(placeholder, sources, keysInResolution, unresolved);
    result.append(resolved);
  }

  private String resolvePlaceholder(String placeholder, List<PropertySource> sources,
      Set<String> keysInResolution, Set<String> unresolved) {
    var colonIndex = placeholder.indexOf(delimiter);
    var key = colonIndex >= 0 ? placeholder.substring(0, colonIndex) : placeholder;
    Supplier<String> fallbackSupplier = getFallback(placeholder, colonIndex);

    if (!keysInResolution.add(key)) {
      throw new IllegalStateException("circular reference detected for key: " + key);
    }

    String resolved = resolveKey(key, fallbackSupplier, sources, keysInResolution, unresolved);
    keysInResolution.remove(key);
    return resolved;
  }

  private Supplier<String> getFallback(String placeholder, int colonIndex) {
    return colonIndex >= 0
        ? () -> placeholder.substring(colonIndex + delimiter.length())
        : () -> null;
  }

  private String resolveKey(String key, Supplier<String> fallbackSupplier,
      List<PropertySource> sources,
      Set<String> keysInResolution,
      Set<String> unresolved) {
    String value = resolve(key, sources);
    if (value != null) {
      return replace(value, sources, keysInResolution, unresolved);
    }

    String fallback = fallbackSupplier.get();
    if (fallback != null) {
      return replace(fallback, sources, keysInResolution, unresolved);
    }

    unresolved.add(key);
    return prefix + key + suffix;
  }

  private String resolve(String key, List<PropertySource> sources) {
    if (isBlank(key)) {
      return null;
    }

    for (var source : sources) {
      String value = source.get(key);
      if (value != null) {
        return value;
      }
    }

    return null;
  }
}
