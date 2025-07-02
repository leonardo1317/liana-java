package io.github.liana.config;

import static io.github.liana.internal.PlaceholderUtils.replace;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;

public final class ConfigInterpolator {

  private static final ObjectMapper mapper = ObjectMapperProvider.getJsonInstance();

  private ConfigInterpolator() {
  }

  public static Map<String, Object> of(Map<String, Object> source, Map<String, String> variables) {
    JsonNode root = mapper.convertValue(source, JsonNode.class);
    interpolateTextNodes(root, variables);
    return mapper.convertValue(root, new TypeReference<>() {
    });
  }

  private static void interpolateTextNodes(JsonNode node, Map<String, String> variables) {
    if (node.isObject()) {
      ObjectNode objectNode = (ObjectNode) node;
      interpolateTextNodesForObjects(objectNode, variables);
    } else if (node.isArray()) {
      ArrayNode array = (ArrayNode) node;
      interpolateTextNodesForArrays(array, variables);
    }
  }

  private static void interpolateTextNodesForObjects(ObjectNode objectNode,
      Map<String, String> variables) {
    Set<Map.Entry<String, JsonNode>> properties = objectNode.properties();
    for (Map.Entry<String, JsonNode> entry : properties) {
      applyInterpolatedValue(
          interpolated -> objectNode.set(entry.getKey(), safeTextNode(interpolated)),
          entry.getValue(), variables);
    }
  }

  private static void interpolateTextNodesForArrays(ArrayNode array,
      Map<String, String> variables) {
    for (int i = 0; i < array.size(); i++) {
      int index = i;
      applyInterpolatedValue(interpolated -> array.set(index, safeTextNode(interpolated)),
          array.get(i), variables);
    }
  }

  private static void applyInterpolatedValue(Consumer<String> consumer, JsonNode value,
      Map<String, String> variables) {
    if (!value.isTextual()) {
      interpolateTextNodes(value, variables);
      return;
    }

    String original = value.asText();
    String interpolated = interpolate(original, variables);
    if (!Objects.equals(interpolated, original)) {
      consumer.accept(interpolated);
    }
  }

  private static JsonNode safeTextNode(String value) {
    return value == null ? NullNode.getInstance() : TextNode.valueOf(value);
  }

  private static String interpolate(String input, Map<String, String> variables) {

    return replace(input, variables);
  }
}
