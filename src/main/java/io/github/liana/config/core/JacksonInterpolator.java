package io.github.liana.config.core;

import static java.util.Objects.requireNonNull;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.github.liana.config.api.Placeholder;
import io.github.liana.config.core.exception.ConversionException;
import io.github.liana.config.internal.ImmutableConfigMap;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

/**
 * Replaces placeholders in a map with provided variables.
 *
 * <p>This class processes a map of values, replacing any placeholders in text
 * (like "${USER}") with corresponding values from a variables map. Nested maps and lists are
 * supported. The original map is not modified; a new map is returned.</p>
 *
 * <p>Example:</p>
 *
 * <pre>{@code
 * Map<String, Object> input = Map.of(
 *     "user", "${USER}",
 *     "password", "${PASS}"
 * );
 *
 * Placeholder placeholder = Placeholder.of("${", "}");
 * Map<String, String> vars = Map.of(
 *     "USER", "Alice",
 *     "PASS", "secret"
 * );
 *
 * JacksonInterpolator interpolator = new JacksonInterpolator(new ObjectMapper());
 * Map<String, Object> result = interpolator.interpolate(input, placeholder, vars);
 *
 * }</pre>
 */
public final class JacksonInterpolator extends AbstractJacksonComponent {

  /**
   * Creates a new interpolator with the given {@link ObjectMapper}.
   *
   * @param mapper the mapper to use for conversions; must not be null
   */
  public JacksonInterpolator(ObjectMapper mapper) {
    super(mapper);
  }

  /**
   * Interpolates all textual values in the given map, replacing placeholders that can be fully
   * resolved using the provided variables.
   *
   * <p>If the input map or variable map is empty, the original map is returned as an unmodifiable
   * view without any modifications.
   *
   * @param source      the source map to interpolate; must not be null
   * @param placeholder the placeholder definition (prefix/suffix) to use; must not be null
   * @param variables   the variables to resolve placeholders with; must not be null
   * @return a new unmodifiable map with interpolated values, or the unmodified source if there is
   * nothing to interpolate
   * @throws NullPointerException  if {@code source}, {@code placeholder}, or {@code variables} is
   *                               null
   * @throws ConversionException   if conversion between map and JSON tree fails
   * @throws IllegalStateException if the interpolation produces an unexpected null value
   */
  public Map<String, Object> interpolate(Map<String, Object> source, Placeholder placeholder,
      ImmutableConfigMap variables) {

    requireNonNull(source, "source map must not be null");
    requireNonNull(placeholder, "placeholder must not be null");
    requireNonNull(variables, "variables map must not be null");

    if (source.isEmpty() || variables.isEmpty()) {
      return Collections.unmodifiableMap(source);
    }

    JsonNode root = executeWithResult(() -> mapper.convertValue(source, JsonNode.class),
        "failed to prepare data for interpolation");

    processNode(root, placeholder, variables);
    return executeWithResult(
        () -> mapper.convertValue(root, MAP_TYPE),
        "failed to finalize interpolated data"
    );
  }

  /**
   * Recursively processes a JSON node, delegating to object or array processing as appropriate.
   *
   * @param node        the node to process; may be null
   * @param placeholder the placeholder definition; must not be null
   * @param variables   the variables to resolve placeholders with; must not be null
   * @throws IllegalStateException if the node is null, indicating an unexpected state in the
   *                               interpolation process
   */
  private void processNode(JsonNode node, Placeholder placeholder,
      ImmutableConfigMap variables) {

    Optional.ofNullable(node)
        .orElseThrow(() -> new IllegalStateException(
            "unexpected null value encountered during interpolation"));

    if (node.isObject()) {
      processObject((ObjectNode) node, placeholder, variables);
    } else if (node.isArray()) {
      processArray((ArrayNode) node, placeholder, variables);
    }
  }

  /**
   * Processes all properties of a JSON object node, applying interpolation to textual values and
   * recursively visiting nested nodes.
   *
   * @param node        the object node to process; must not be null
   * @param placeholder the placeholder definition; must not be null
   * @param variables   the variables to resolve placeholders with; must not be null
   */
  private void processObject(ObjectNode node, Placeholder placeholder,
      ImmutableConfigMap variables) {
    Set<Map.Entry<String, JsonNode>> properties = node.properties();
    for (Map.Entry<String, JsonNode> entry : properties) {
      applyIfTextual(entry.getValue(),
          interpolated -> node.set(entry.getKey(), textNode(interpolated)),
          placeholder, variables);
    }
  }

  /**
   * Processes all elements of a JSON array node, applying interpolation to textual values and
   * recursively visiting nested nodes.
   *
   * @param node        the array node to process; must not be null
   * @param placeholder the placeholder definition; must not be null
   * @param variables   the variables to resolve placeholders with; must not be null
   */
  private void processArray(ArrayNode node, Placeholder placeholder,
      ImmutableConfigMap variables) {
    for (int i = 0; i < node.size(); i++) {
      final int index = i;
      applyIfTextual(node.get(i), interpolated -> node.set(index, textNode(interpolated)),
          placeholder, variables);
    }
  }

  /**
   * Applies interpolation to a node if it is textual, or delegates back to recursive processing if
   * it is an object or array.
   *
   * @param node        the node to evaluate; must not be null
   * @param consumer    consumer to apply if interpolation produces a new value
   * @param placeholder the placeholder definition; must not be null
   * @param variables   the variables to resolve placeholders with; must not be null
   */
  private void applyIfTextual(JsonNode node, Consumer<String> consumer,
      Placeholder placeholder,
      ImmutableConfigMap variables) {
    if (!node.isTextual()) {
      processNode(node, placeholder, variables);
      return;
    }

    String original = node.asText();
    if (original.isEmpty()) {
      return;
    }

    String interpolated = placeholder.replaceIfAllResolvable(original, variables.toMap()).orElse(original);
    if (!Objects.equals(interpolated, original)) {
      consumer.accept(interpolated);
    }
  }
}
