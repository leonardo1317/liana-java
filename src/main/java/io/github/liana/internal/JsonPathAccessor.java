package io.github.liana.internal;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.liana.config.ObjectMapperProvider;
import io.github.liana.config.exception.ConversionException;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

import static java.util.Objects.requireNonNull;

public final class JsonPathAccessor {

    private static final ObjectMapper mapper = ObjectMapperProvider.getJsonInstance();

    private JsonPathAccessor() {
    }

    public static <T> Optional<T> get(Object source, Type targetType) {
        requireNonNull(source, "Source object cannot be null");
        requireNonNull(targetType, "Target type cannot be null");

        JsonNode node = getNode(source);
        if (node.isMissingNode()) {
            return Optional.empty();
        }

        return Optional.ofNullable(convertValue(node, constructJavaType(targetType)));
    }

    public static <T> Optional<T> get(Object source, String path, Type targetType) {
        requireNonNull(source, "Source object cannot be null");
        requireNonNull(path, "Path cannot be null");
        requireNonNull(targetType, "Target type cannot be null");

        JsonNode node = getNode(source, path);
        if (node.isMissingNode()) {
            return Optional.empty();
        }

        return Optional.ofNullable(convertValue(node, constructJavaType(targetType)));
    }

    public static <E> List<E> getList(Object source, String path, Class<E> targetType) {
        requireNonNull(source, "Source object cannot be null");
        requireNonNull(path, "Path cannot be null");
        requireNonNull(targetType, "Target type cannot be null");

        JsonNode node = getNode(source, path);
        if (node.isMissingNode() || !node.isArray()) {
            return Collections.emptyList();
        }

        JavaType listType = safeConvert(
                () -> mapper.getTypeFactory().constructCollectionType(List.class, targetType),
                String.format("Invalid or unsupported target list type: %s", targetType.getName())
        );

        List<E> list = convertValue(node, listType);
        return Optional.ofNullable(list).orElse(Collections.emptyList());
    }

    public static <V> Map<String, V> getMap(Object source, String path, Class<V> targetType) {
        requireNonNull(source, "Source object cannot be null");
        requireNonNull(path, "Path cannot be null");

        JsonNode node = getNode(source, path);
        if (node.isMissingNode() || !node.isObject()) {
            return Collections.emptyMap();
        }

        JavaType mapType = safeConvert(
                () -> mapper.getTypeFactory().constructMapType(Map.class, String.class, targetType),
                String.format("Invalid or unsupported target map type: %s", targetType.getName())
        );

        Map<String, V> resultMap = convertValue(node, mapType);
        return Optional.ofNullable(resultMap).orElse(Collections.emptyMap());
    }

    public static boolean hasPath(Object source, String path) {
        requireNonNull(source, "Source object cannot be null");
        requireNonNull(path, "Path cannot be null");

        JsonNode node = getNode(source, path);
        return !node.isMissingNode();
    }

    private static JsonNode getNode(Object source, String path) {
        JsonNode tree = getNode(source);

        String jsonPointer = toJsonPointer(path);

        return safeConvert(
                () -> tree.at(jsonPointer),
                String.format("Invalid JSON pointer generated from path: %s", path)
        );
    }

    private static JsonNode getNode(Object source) {
        return safeConvert(
                () -> mapper.valueToTree(source),
                String.format("Failed to convert source to JSON tree. Source type: %s", source.getClass().getName())
        );
    }

    private static JavaType constructJavaType(Type targetType) {
        return safeConvert(
                () -> mapper.constructType(targetType),
                String.format("Invalid or unsupported target type: %s", targetType.getTypeName())
        );
    }


    private static String toJsonPointer(String path) {
        return "/" + path.replace(".", "/")
                .replaceAll("\\[(\\d+)]", "/$1");
    }

    private static <T> T convertValue(Object value, JavaType type) {

        return safeConvert(
                () -> mapper.convertValue(value, type),
                String.format("Failed to convert value to target type: %s",
                        type.getTypeName())
        );
    }

    private static <T> T safeConvert(Supplier<T> supplier, String errorMessage) {
        try {
            return supplier.get();
        } catch (RuntimeException e) {
            throw new ConversionException(errorMessage);
        }
    }
}
