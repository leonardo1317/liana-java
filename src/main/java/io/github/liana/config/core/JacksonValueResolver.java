package io.github.liana.config.core;

import static java.util.Objects.requireNonNull;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.liana.config.core.exception.ConversionException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Resolves configuration values from a source map or JSON input using Jackson.
 *
 * <p>This implementation stores the source as a {@link com.fasterxml.jackson.databind.JsonNode}
 * tree,
 * caches resolved nodes, and performs type-safe conversion for single values, lists, and maps.
 *
 * <p>Instances are immutable and thread-safe for read operations.
 *
 * <p>Construction options:
 * <ul>
 *   <li>From a {@link Map} using the default {@link ObjectMapper}</li>
 *   <li>From a {@link Map} with a custom {@link ObjectMapper}</li>
 *   <li>From a JSON {@link InputStream} with a custom {@link ObjectMapper}</li>
 * </ul>
 *
 * <p>Conversion or parsing failures are wrapped in {@link ConversionException}.
 */
public final class JacksonValueResolver extends AbstractJacksonComponent implements ValueResolver {

  private static final String MSG_SOURCE_NULL = "source must not be null";
  private static final String MSG_INPUT_STREAM_NULL = "inputStream must not be null";
  private static final String MSG_KEY_NULL = "key must not be null";
  private static final String MSG_TARGET_TYPE_NULL = "target type must not be null";
  private static final String MSG_INVALID_TARGET_LIST_TYPE = "invalid or unsupported target list type: %s";
  private static final String MSG_INVALID_TARGET_MAP_TYPE = "invalid or unsupported target map type: %s";
  private static final String MSG_INVALID_TARGET_TYPE = "invalid or unsupported target type: %s";
  private static final String MSG_INVALID_JSON_POINTER = "invalid JSON pointer generated from key: %s";
  private static final String MSG_CONVERT_VALUE =
      "failed to convert value to target type: %s";
  private static final ObjectMapper DEFAULT_MAPPER = JacksonMappers.create().getJson();
  private final LoadingCache<String, JsonNode> cache = new LoadingCache<>();
  private final JsonNode source;

  /**
   * Creates a new resolver using a custom {@link ObjectMapper} and JSON {@link InputStream}.
   *
   * @param mapper      the {@link ObjectMapper} to use (must not be null)
   * @param inputStream the {@link InputStream} containing JSON data (must not be null)
   * @throws NullPointerException if any argument is null
   * @throws ConversionException  if reading or parsing the JSON fails
   */
  public JacksonValueResolver(ObjectMapper mapper, InputStream inputStream) {
    super(mapper);
    this.source = executeWithResult(
        () -> mapper.readTree(requireNonNull(inputStream, MSG_INPUT_STREAM_NULL)),
        String.format(MSG_CONVERT_VALUE, inputStream.getClass().getTypeName()));
  }

  /**
   * Creates a new {@code JacksonValueResolver} from the given source map.
   *
   * @param source the source configuration map (must not be {@code null})
   * @throws NullPointerException if {@code source} is {@code null}
   * @throws ConversionException  if the source cannot be converted into a JSON tree
   */
  public JacksonValueResolver(Map<String, Object> source) {
    this(DEFAULT_MAPPER, source);
  }

  /**
   * Creates a new {@code JacksonValueResolver} with a custom {@link ObjectMapper}.
   *
   * <p>This constructor allows specifying a custom mapper for advanced
   * serialization/deserialization behavior.
   *
   * @param mapper the {@link ObjectMapper} to use (must not be {@code null})
   * @param source the source configuration map (must not be {@code null})
   * @throws NullPointerException if {@code mapper} or {@code source} is {@code null}
   * @throws ConversionException  if the source cannot be converted into a JSON tree
   */
  public JacksonValueResolver(ObjectMapper mapper, Map<String, Object> source) {
    super(mapper);
    this.source = executeWithResult(
        () -> mapper.valueToTree(requireNonNull(source, MSG_SOURCE_NULL)),
        String.format(MSG_CONVERT_VALUE, source.getClass().getTypeName()));
  }

  @Override
  public boolean containsKey(String key) {
    requireNonNull(key, MSG_KEY_NULL);
    JsonNode node = find(key);
    return !node.isMissingNode();
  }

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

  @Override
  public Map<String, Object> getRootAsMap() {
    JavaType type = constructJavaType(MAP_TYPE.getType());
    return Collections.unmodifiableMap(new LinkedHashMap<>(convertValue(source, type)));
  }

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
    return cache.getOrCompute("__ROOT__", () -> source);
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
