package io.github.liana.internal;

import static java.util.Objects.requireNonNull;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Utility class for operations related to {@link Map} creation and manipulation.
 *
 * <p>This class is not instantiable.
 */
public final class MapUtils {

  private MapUtils() {
  }

  /**
   * Converts an array of strings representing key-value pairs into a {@link Map}.
   *
   * <p>The array must contain an even number of elements, where even-indexed elements are keys and
   * odd-indexed elements are their corresponding values.
   *
   * <p>For example, an array {@code ["key1", "value1", "key2", "value2"]} will be converted into a
   * map
   * with two entries: "key1" -> "value1" and "key2" -> "value2".
   *
   * @param entries an array of key-value pairs (must not be null and must have an even length)
   * @return a {@link LinkedHashMap} containing the key-value pairs in insertion order
   * @throws NullPointerException     if {@code entries} is null
   * @throws IllegalArgumentException if {@code entries} has an odd length (i.e., a key without a
   *                                  corresponding value)
   */
  public static Map<String, String> toMap(String[] entries) {
    requireNonNull(entries, "entries must not be null");

    if (entries.length % 2 != 0) {
      throw new IllegalArgumentException("Missing value for key: " + entries[entries.length - 1]);
    }

    Map<String, String> map = new LinkedHashMap<>();
    for (int i = 0; i < entries.length; i += 2) {
      String key = entries[i];
      String value = entries[i + 1];
      map.put(key, value);
    }

    return map;
  }
}
