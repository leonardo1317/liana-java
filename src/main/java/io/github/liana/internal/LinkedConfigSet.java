package io.github.liana.internal;

import java.util.Collection;
import java.util.LinkedHashSet;

import static io.github.liana.internal.StringUtils.isBlank;
import static java.util.Objects.requireNonNull;

/**
 * A specialized {@link LinkedHashSet} for configuration strings that:
 * <ul>
 *     <li>Preserves insertion order</li>
 *     <li>Rejects blank or null strings</li>
 *     <li>Filters invalid entries on bulk addition</li>
 * </ul>
 */
public class LinkedConfigSet extends LinkedHashSet<String> {

    /**
     * Constructs an empty {@code LinkedConfigSet}, using default validation rules.
     * Keys and values must be non-blank.
     */
    public LinkedConfigSet() {
    }

    /**
     * Constructs a {@code LinkedConfigSet} from the given collection.
     * All blank or null values are ignored.
     *
     * @param collection the initial collection of strings (may be empty but not {@code null})
     * @throws NullPointerException if {@code collection} is {@code null}
     */
    public LinkedConfigSet(Collection<? extends String> collection) {
        addAll(collection);
    }

    /**
     * Adds all non-blank strings from the given collection to the set.
     * <p>
     * Null or blank strings are ignored. The insertion order of elements
     * in this set will follow the iteration order of the input collection.
     * If the input collection does not guarantee a consistent iteration order
     * (e.g., {@link java.util.HashSet}), the resulting order in this set may be unpredictable.
     *
     * @param collection the collection of strings to add (must not be {@code null})
     * @return {@code true} if the set changed as a result of the call
     * @throws NullPointerException if {@code collection} is {@code null}
     */
    @Override
    public boolean addAll(Collection<? extends String> collection) {
        requireNonNull(collection, "collection must not be null");
        boolean modified = false;
        for (String value : collection) {
            if (add(value)) {
                modified = true;
            }
        }

        return modified;
    }

    /**
     * Adds a string to the set if it is non-blank and non-null.
     *
     * @param value the string to add
     * @return {@code true} if the set changed as a result of the call
     */
    @Override
    public boolean add(String value) {
        return !isBlank(value) && super.add(value);
    }
}
