package io.github.liana.config;

import static java.util.Objects.requireNonNull;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.TextNode;
import io.github.liana.config.exception.ConversionException;
import io.github.liana.config.exception.MergeException;
import java.io.IOException;
import java.util.Map;
import java.util.function.Supplier;

abstract class AbstractJacksonComponent {

  protected static final TypeReference<Map<String, Object>> MAP_TYPE = new TypeReference<>() {
  };

  protected final ObjectMapper mapper;

  protected AbstractJacksonComponent(ObjectMapper mapper) {
    this.mapper = requireNonNull(mapper, "mapper must not be null");
  }

  protected JsonNode textNode(String value) {
    return value == null ? NullNode.getInstance() : TextNode.valueOf(value);
  }

  /**
   * Safely converts a value using a Supplier that may throw a runtime exception. Wraps any
   * exception in a ConversionException to ensure consistency across components.
   *
   * @param supplier     the conversion logic to execute
   * @param errorMessage message to include in case of failure
   * @param <T>          type of the result
   * @return the converted value
   * @throws ConversionException if the conversion fails
   * @throws RuntimeException    if an unexpected runtime exception occurs during the conversion
   */
  protected <T> T executeWithResult(Supplier<T> supplier, String errorMessage) {
    try {
      return supplier.get();
    } catch (IllegalArgumentException e) {
      throw new ConversionException(errorMessage, e);
    } catch (RuntimeException e) {
      throw new ConversionException("unexpected error during ", e);
    }
  }

  protected void executeAction(ThrowingRunnable action, String errorMessage) {
    try {
      action.run();
    } catch (IOException e) {
      throw new MergeException(errorMessage, e);
    } catch (Exception e) {
      throw new MergeException("unexpected error during ", e);
    }
  }
}
