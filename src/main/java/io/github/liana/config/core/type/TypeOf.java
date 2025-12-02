package io.github.liana.config.core.type;

import static java.util.Objects.requireNonNull;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Captures and preserves generic type information at runtime.
 *
 * <p>This abstract class allows retrieval of the generic type parameter {@code T} by subclassing
 * via anonymous class instantiation. This approach overcomes Java's type erasure limitation at
 * runtime.
 *
 * <pre>{@code
 * TypeOf<List<String>> type = new TypeOf<List<String>>() {};
 * Type capturedType = type.getType();
 * }</pre>
 *
 * @param <T> the generic type to capture
 */
public abstract class TypeOf<T> {

  private final Type type;

  /**
   * Constructs a new {@code TypeOf} instance and captures the generic type parameter.
   *
   * <p>This constructor must be invoked via an anonymous class to properly capture the type
   * parameter. Direct subclassing without specifying the generic type will result in an exception.
   *
   * @throws IllegalArgumentException if the class is not properly parameterized as an anonymous
   *                                  class with a concrete type.
   * @throws NullPointerException     if the captured type is {@code null}.
   */
  protected TypeOf() {

    Type superclass = getClass().getGenericSuperclass();

    if (!(superclass instanceof ParameterizedType)) {
      throw new IllegalArgumentException(
          "TypeOf must be parameterized and instantiated as an anonymous class."
      );
    }

    this.type = ((ParameterizedType) superclass).getActualTypeArguments()[0];

    requireNonNull(this.type, "The type captured by TypeOf cannot be null.");
  }

  /**
   * Returns the captured generic {@link Type} instance.
   *
   * @return the captured type
   */
  public Type getType() {
    return type;
  }
}
