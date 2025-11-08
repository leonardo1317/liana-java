package io.github.liana.config;

import java.util.List;
import java.util.Map;

public interface ConfigResourceLocationBuilder {

  ConfigResourceLocationBuilder provider(String provider);

  ConfigResourceLocationBuilder baseDirectories(String... baseDirectories);

  ConfigResourceLocationBuilder addResource(String resourceName);

  ConfigResourceLocationBuilder addResources(String... resources);

  ConfigResourceLocationBuilder addResourceFromList(List<String> resources);

  ConfigResourceLocationBuilder addVariable(String key, String value);

  ConfigResourceLocationBuilder addVariables(String... variables);

  ConfigResourceLocationBuilder addVariablesFromMap(Map<String, String> variables);

  ConfigResourceLocationBuilder verboseLogging(boolean verboseLogging);

  ConfigResourceLocationBuilder placeholders(Placeholder placeholder);

  ConfigResourceLocation build();
}
