package io.github.liana.config;

import static java.util.Objects.requireNonNull;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.liana.config.exception.ConversionException;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Resolves configuration values from a source map using Jackson for JSON tree navigation.
 *
 * <p>This implementation converts the source map into a
 * {@link com.fasterxml.jackson.databind.JsonNode} tree and supports type-safe retrieval of values,
 * lists, and maps, as well as conversion of the entire root configuration into a specified type.
 *
 * <p>Instances of this class are immutable and thread-safe for read operations.
 */
final class JacksonValueResolver extends AbstractJacksonComponent implements ValueResolver {

  private static final String MSG_SOURCE_NULL = "source must not be null";
  private static final String MSG_KEY_NULL = "key must not be null";
  private static final String MSG_TARGET_TYPE_NULL = "target type must not be null";
  private static final String MSG_INVALID_TARGET_LIST_TYPE = "invalid or unsupported target list type: %s";
  private static final String MSG_INVALID_TARGET_MAP_TYPE = "invalid or unsupported target map type: %s";
  private static final String MSG_INVALID_TARGET_TYPE = "invalid or unsupported target type: %s";
  private static final String MSG_INVALID_JSON_POINTER = "invalid JSON pointer generated from key: %s";
  private static final String MSG_CONVERT_SOURCE_TO_TREE =
      "failed to convert source to JSON tree. Source type: %s";
  private static final String MSG_CONVERT_VALUE =
      "failed to convert value to target type: %s";
  private final LoadingCache<String, JsonNode> cache = new LoadingCache<>();
  private final Map<String, Object> source;

  /**
   * Creates a new {@code JacksonValueResolver} from the given source map.
   *
   * @param source the source configuration map (must not be {@code null})
   * @throws NullPointerException if {@code source} is {@code null}
   * @throws ConversionException  if the source cannot be converted into a JSON tree
   */
  JacksonValueResolver(Map<String, Object> source) {
    this(JacksonMappers.create().getJson(), source);
  }

  /**
   * Creates a new {@code JacksonValueResolver} with a custom {@link ObjectMapper}.
   *
   * <p>This constructor allows specifying a custom mapper for advanced
   * serialization/deserialization
   * behavior.
   *
   * @param mapper the {@link ObjectMapper} to use (must not be {@code null})
   * @param source the source configuration map (must not be {@code null})
   * @throws NullPointerException if {@code mapper} or {@code source} is {@code null}
   * @throws ConversionException  if the source cannot be converted into a JSON tree
   */
  JacksonValueResolver(ObjectMapper mapper, Map<String, Object> source) {
    super(mapper);
    this.source = Collections.unmodifiableMap(new LinkedHashMap<>(
        requireNonNull(source, MSG_SOURCE_NULL)
    ));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean containsKey(String key) {
    requireNonNull(key, MSG_KEY_NULL);
    JsonNode node = find(key);
    return !node.isMissingNode();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public <T> Optional<T> get(String key, Type targetType) {
    requireNonNull(key, MSG_KEY_NULL);
    requireNonNull(targetType, MSG_TARGET_TYPE_NULL);

    JsonNode node = find(key);
    if (node.isMissingNode()) {
      return Optional.empty();
    }

    return Optional.ofNullable(convertValue(node, constructJavaType(targetType)));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public <E> List<E> getList(String key, Class<E> targetType) {
    requireNonNull(key, MSG_KEY_NULL);
    requireNonNull(targetType, MSG_TARGET_TYPE_NULL);

    JsonNode node = find(key);
    if (node.isMissingNode() || !node.isArray()) {
      return Collections.emptyList();
    }

    JavaType listType = executeWithResult(
        () -> mapper.getTypeFactory().constructCollectionType(List.class, targetType),
        String.format(MSG_INVALID_TARGET_LIST_TYPE, targetType.getName())
    );

    List<E> list = convertValue(node, listType);
    return Optional.ofNullable(list).orElse(Collections.emptyList());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public <V> Map<String, V> getMap(String key, Class<V> targetType) {
    requireNonNull(key, MSG_KEY_NULL);

    JsonNode node = find(key);
    if (node.isMissingNode() || !node.isObject()) {
      return Collections.emptyMap();
    }

    JavaType mapType = executeWithResult(
        () -> mapper.getTypeFactory().constructMapType(Map.class, String.class, targetType),
        String.format(MSG_INVALID_TARGET_MAP_TYPE, targetType.getName())
    );

    Map<String, V> resultMap = convertValue(node, mapType);
    return Optional.ofNullable(resultMap).orElse(Collections.emptyMap());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Map<String, Object> getRootAsMap() {
    return source;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public <T> Optional<T> getRootAs(Type targetType) {
    requireNonNull(targetType, MSG_TARGET_TYPE_NULL);
    JsonNode rootNode = getRootNode();

    if (rootNode.isMissingNode()) {
      return Optional.empty();
    }

    return Optional.ofNullable(convertValue(rootNode, constructJavaType(targetType)));
  }

  private JsonNode find(String key) {
    return cache.getOrCompute(key, () ->
        executeWithResult(
            () -> getRootNode().at(toJsonPointer(key)),
            String.format(MSG_INVALID_JSON_POINTER, key)
        ));
  }

  private JsonNode getRootNode() {
    return cache.getOrCompute("ROOT_NODE",
        () -> executeWithResult(
            () -> mapper.valueToTree(source),
            String.format(MSG_CONVERT_SOURCE_TO_TREE, source.getClass().getName())
        ));
  }

  private JavaType constructJavaType(Type targetType) {
    return executeWithResult(
        () -> mapper.constructType(targetType),
        String.format(MSG_INVALID_TARGET_TYPE, targetType.getTypeName())
    );
  }

  private String toJsonPointer(String key) {
    return "/" + key.replace(".", "/")
        .replaceAll("\\[(\\d+)]", "/$1");
  }

  private <T> T convertValue(Object value, JavaType type) {
    return executeWithResult(
        () -> mapper.convertValue(value, type),
        String.format(MSG_CONVERT_VALUE, type.getTypeName()));
  }
}
