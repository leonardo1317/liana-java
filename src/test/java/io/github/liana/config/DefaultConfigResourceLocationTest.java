package io.github.liana.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.liana.internal.ImmutableConfigMap;
import io.github.liana.internal.ImmutableConfigSet;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DefaultConfigResourceLocationTest {

  @Mock
  private Placeholder placeholder;

  @Test
  @DisplayName("should construct with valid values and return them via getters")
  void shouldConstructAndReturnValues() {
    var baseDirectories = ImmutableConfigSet.of(Set.of("config"));
    var resourceNames = ImmutableConfigSet.of(Set.of("config.yml", "secrets.yml"));
    var variables = ImmutableConfigMap.of(Map.of("env", "prod"));
    final String PROVIDER = "classpath";

    ConfigResourceLocation location = new DefaultConfigResourceLocation(
        PROVIDER,
        baseDirectories,
        resourceNames,
        variables,
        true,
        placeholder
    );

    assertEquals(PROVIDER, location.getProvider());
    assertEquals(baseDirectories, location.getBaseDirectories());
    assertEquals(resourceNames, location.getResourceNames());
    assertEquals(variables, location.getVariables());
    assertTrue(location.isVerboseLogging());
    assertSame(placeholder, location.getPlaceholder());
  }

  @Test
  @DisplayName("should throw NullPointerException when provider is null")
  void shouldThrowWhenProviderIsNull() {
    var baseDirectories = ImmutableConfigSet.of(Set.of("config"));
    var resourceNames = ImmutableConfigSet.of(Set.of("config.yml"));
    var variables = ImmutableConfigMap.of(Map.of("env", "prod"));

    assertThrows(NullPointerException.class, () ->
        new DefaultConfigResourceLocation(null, baseDirectories, resourceNames, variables, false,
            placeholder)
    );
  }

  @Test
  @DisplayName("should throw NullPointerException when baseDirectories is null")
  void shouldThrowWhenBaseDirectoriesIsNull() {
    var resourceNames = ImmutableConfigSet.of(Set.of("config.yml"));
    var variables = ImmutableConfigMap.of(Map.of("env", "prod"));

    assertThrows(NullPointerException.class, () ->
        new DefaultConfigResourceLocation("classpath", null, resourceNames, variables, false,
            placeholder)
    );
  }

  @Test
  @DisplayName("should throw NullPointerException when resourceNames is null")
  void shouldThrowWhenResourceNamesIsNull() {
    var baseDirectories = ImmutableConfigSet.of(Set.of("config"));
    var variables = ImmutableConfigMap.of(Map.of("env", "prod"));

    assertThrows(NullPointerException.class, () ->
        new DefaultConfigResourceLocation("classpath", baseDirectories, null, variables, false,
            placeholder)
    );
  }

  @Test
  @DisplayName("should throw NullPointerException when variables is null")
  void shouldThrowWhenVariablesIsNull() {
    var baseDirectories = ImmutableConfigSet.of(Set.of("config"));
    var resourceNames = ImmutableConfigSet.of(Set.of("config.yml"));

    assertThrows(NullPointerException.class, () ->
        new DefaultConfigResourceLocation("classpath", baseDirectories, resourceNames, null, false,
            placeholder)
    );
  }

  @Test
  @DisplayName("should throw NullPointerException when placeholderResolver is null")
  void shouldThrowWhenPlaceholderResolverIsNull() {
    var baseDirectories = ImmutableConfigSet.of(Set.of("config"));
    var resourceNames = ImmutableConfigSet.of(Set.of("config.yml"));
    var variables = ImmutableConfigMap.of(Map.of("env", "prod"));

    assertThrows(NullPointerException.class, () ->
        new DefaultConfigResourceLocation("classpath", baseDirectories, resourceNames, variables,
            false, null)
    );
  }
}
