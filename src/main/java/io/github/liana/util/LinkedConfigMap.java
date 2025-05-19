package io.github.liana.util;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

import static io.github.liana.util.StringUtils.isBlank;
import static java.util.Objects.requireNonNull;

/**
 * A specialized {@link LinkedHashMap} for configuration data that validates keys and values before insertion.
 * <p>
 * This map:
 * <ul>
 *   <li>Preserves insertion order.</li>
 *   <li>Ensures that all keys are non-blank (i.e., not {@code null} and not composed solely of whitespace).</li>
 *   <li>Ensures that all values are non-blank (i.e., not {@code null} and not composed solely of whitespace).</li>
 * </ul>
 * <p>
 * Any attempt to insert or modify an entry with an invalid key or value will result in an {@link IllegalArgumentException}.
 */
public class LinkedConfigMap extends LinkedHashMap<String, String> {

    /**
     * Constructs an empty {@code LinkedConfigMap}, using default validation rules.
     * Keys and values must be non-blank.
     */
    public LinkedConfigMap() {
    }

    /**
     * Creates a {@code LinkedConfigMap} initialized with the entries from the given map.
     * <p>
     * This constructor delegates to {@link #putAll(Map)}, so:
     * <ul>
     *   <li>All entries in the provided map are validated before insertion.</li>
     *   <li>Insertion order of the entries is preserved.</li>
     * </ul>
     *
     * @param map the map whose entries are to be placed into this map.
     * @throws NullPointerException     if {@code map} is {@code null}.
     * @throws IllegalArgumentException if any key or value in the map is invalid (null, empty, or blank).
     */
    public LinkedConfigMap(Map<? extends String, ? extends String> map) {
        this.putAll(map);
    }

    /**
     * Associates the specified value with the specified key in this map after validating both.
     *
     * @param key   the key with which the specified value is to be associated.
     * @param value the value to be associated with the specified key.
     * @return the previous value associated with key, or {@code null} if there was no mapping.
     * @throws IllegalArgumentException if the key or value is invalid.
     */
    @Override
    public String put(String key, String value) {
        validateKeyValue(key, value);

        return super.put(key, value);
    }

    /**
     * Copies all of the mappings from the specified map to this map after validating each entry.
     * <p>
     * The mappings are inserted in the order returned by the specified map's entry set iterator,
     * thus preserving insertion order.
     *
     * @param map mappings to be stored in this map.
     * @throws NullPointerException     if {@code map} is {@code null}.
     * @throws IllegalArgumentException if any key or value in the input map is invalid (null, empty, or blank).
     */
    @Override
    public void putAll(Map<? extends String, ? extends String> map) {
        requireNonNull(map, "map must not be null");
        Map<String, String> temp = new LinkedHashMap<>();
        for (Map.Entry<? extends String, ? extends String> entry : map.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            validateKeyValue(key, value);
            temp.put(key, value);
        }

        super.putAll(temp);
    }

    /**
     * If the specified key is not already associated with a value, associates it with the given value
     * after validation.
     *
     * @param key   key with which the specified value is to be associated.
     * @param value value to be associated with the specified key.
     * @return the previous value associated with the key, or {@code null} if there was no mapping.
     * @throws IllegalArgumentException if the key or value is invalid.
     */
    @Override
    public String putIfAbsent(String key, String value) {
        validateKeyValue(key, value);
        return super.putIfAbsent(key, value);
    }

    /**
     * Replaces the entry for the specified key only if currently mapped to the specified old value,
     * after validating the new value.
     *
     * @param key      key with which the specified value is associated.
     * @param oldValue value expected to be associated with the specified key.
     * @param newValue value to be associated with the specified key.
     * @return {@code true} if the value was replaced.
     * @throws IllegalArgumentException if the key or new value is invalid.
     */
    @Override
    public boolean replace(String key, String oldValue, String newValue) {
        validateKeyValue(key, newValue);

        return super.replace(key, oldValue, newValue);
    }

    /**
     * Replaces the entry for the specified key only if it is currently mapped to some value,
     * after validating the new value.
     *
     * @param key   key with which the specified value is associated.
     * @param value value to be associated with the specified key.
     * @return the previous value associated with the specified key, or {@code null} if none.
     * @throws IllegalArgumentException if the key or value is invalid.
     */
    @Override
    public String replace(String key, String value) {
        validateKeyValue(key, value);

        return super.replace(key, value);
    }

    /**
     * Attempts to compute a mapping for the specified key using the given remapping function.
     * If the result is non-null, it is validated before being stored.
     *
     * @param key               key with which the specified value is to be associated.
     * @param remappingFunction function to compute a value.
     * @return the new value associated with the specified key, or {@code null} if none.
     * @throws IllegalArgumentException if the key or computed value is invalid.
     */
    @Override
    public String compute(String key, BiFunction<? super String, ? super String, ? extends String> remappingFunction) {
        String result = super.compute(key, remappingFunction);
        if (result != null) {
            validateKeyValue(key, result);
        }

        return result;
    }

    /**
     * If the specified key is not already associated with a value (or is mapped to null), attempts to compute its value.
     * If the result is non-null, it is validated before being stored.
     *
     * @param key             key with which the computed value is to be associated.
     * @param mappingFunction function to compute a value.
     * @return the current (existing or computed) value associated with the key, or {@code null} if none.
     * @throws IllegalArgumentException if the key or computed value is invalid.
     */
    @Override
    public String computeIfAbsent(String key, Function<? super String, ? extends String> mappingFunction) {
        String result = super.computeIfAbsent(key, mappingFunction);
        if (result != null) {
            validateKeyValue(key, result);
        }

        return result;
    }

    /**
     * If the value for the specified key is present and non-null, attempts to compute a new mapping.
     * If the result is non-null, it is validated before being stored.
     *
     * @param key               key with which the specified value is to be associated.
     * @param remappingFunction function to compute a new value.
     * @return the new value associated with the specified key, or {@code null} if none.
     * @throws IllegalArgumentException if the key or computed value is invalid.
     */
    @Override
    public String computeIfPresent(String key, BiFunction<? super String, ? super String, ? extends String> remappingFunction) {
        String result = super.computeIfPresent(key, remappingFunction);
        if (result != null) {
            validateKeyValue(key, result);
        }

        return result;
    }

    /**
     * Attempts to merge the given value with the existing value for the specified key using the remapping function.
     * If the result is non-null, it is validated before being stored.
     *
     * @param key               key with which the resulting value is to be associated.
     * @param value             value to be merged with the existing value.
     * @param remappingFunction function to recompute a value if present.
     * @return the new value associated with the specified key, or {@code null} if none.
     * @throws IllegalArgumentException if the key or merged value is invalid.
     */
    @Override
    public String merge(String key, String value, BiFunction<? super String, ? super String, ? extends String> remappingFunction) {
        String result = super.merge(key, value, remappingFunction);
        if (result != null) {
            validateKeyValue(key, result);
        }

        return result;
    }

    /**
     * Validates a single key and value.
     *
     * @param key   the key to validate.
     * @param value the value to validate.
     * @throws IllegalArgumentException if the key or value is invalid.
     */
    private void validateKeyValue(String key, String value) {
        if (isBlank(key)) {
            throw new IllegalArgumentException("Invalid key: " + key);
        }

        if (isBlank(value)) {
            throw new IllegalArgumentException("Invalid value for key '" + key + "'");
        }
    }
}
