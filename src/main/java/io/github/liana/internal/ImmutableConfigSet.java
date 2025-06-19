package io.github.liana.internal;

import java.util.Collections;
import java.util.Objects;
import java.util.Set;

public class ImmutableConfigSet {
    private final Set<String> set;

    private ImmutableConfigSet(Set<String> set) {
        this.set = Collections.unmodifiableSet(set);
    }

    public static ImmutableConfigSet empty() {
        return new ImmutableConfigSet(Collections.emptySet());
    }

    public static ImmutableConfigSet of(Set<String> set) {
        return new ImmutableConfigSet(new LinkedConfigSet(set));
    }

    /**
     * Checks if the set contains the given value.
     *
     * @param value a non-null value
     * @return {@code true} if present; {@code false} otherwise
     */
    public boolean contains(String value) {
        return set.contains(value);
    }

    /**
     * Returns {@code true} if the set is empty.
     *
     * @return {@code true} if empty; {@code false} otherwise
     */
    public boolean isEmpty() {
        return set.isEmpty();
    }

    /**
     * Returns the number of values in the set.
     *
     * @return the size of the set
     */
    public int size() {
        return set.size();
    }

    public Set<String> toSet() {
        return set;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ImmutableConfigSet)) return false;
        ImmutableConfigSet that = (ImmutableConfigSet) o;
        return Objects.equals(set, that.set);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(set);
    }

    @Override
    public String toString() {
        return "ImmutableConfigSet" + set.toString();
    }
}
