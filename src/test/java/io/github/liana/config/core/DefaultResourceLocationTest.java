package io.github.liana.config.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.liana.config.api.ResourceLocation;
import io.github.liana.config.api.Placeholder;
import io.github.liana.config.internal.ImmutableConfigMap;
import io.github.liana.config.internal.ImmutableConfigSet;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DefaultResourceLocationTest {

  @Mock
  private Placeholder placeholder;

  @Test
  @DisplayName("should construct with valid values and return them via getters")
  void shouldConstructAndReturnValues() {
    var baseDirectories = ImmutableConfigSet.of(Set.of("config"));
    var resourceNames = ImmutableConfigSet.of(Set.of("config.yml", "secrets.yml"));
    var variables = ImmutableConfigMap.of(Map.of("env", "prod"));
    final String PROVIDER = "classpath";

    ResourceLocation location = new DefaultResourceLocation(
        PROVIDER,
        baseDirectories,
        resourceNames,
        variables,
        true,
        placeholder
    );

    assertEquals(PROVIDER, location.provider());
    assertEquals(baseDirectories, location.baseDirectories());
    assertEquals(resourceNames, location.resourceNames());
    assertEquals(variables, location.variables());
    assertTrue(location.verboseLogging());
    assertSame(placeholder, location.placeholder());
  }

  @Test
  @DisplayName("should throw NullPointerException when provider is null")
  void shouldThrowWhenProviderIsNull() {
    var baseDirectories = ImmutableConfigSet.of(Set.of("config"));
    var resourceNames = ImmutableConfigSet.of(Set.of("config.yml"));
    var variables = ImmutableConfigMap.of(Map.of("env", "prod"));

    assertThrows(NullPointerException.class, () ->
        new DefaultResourceLocation(null, baseDirectories, resourceNames, variables, false,
            placeholder)
    );
  }

  @Test
  @DisplayName("should throw NullPointerException when baseDirectories is null")
  void shouldThrowWhenBaseDirectoriesIsNull() {
    var resourceNames = ImmutableConfigSet.of(Set.of("config.yml"));
    var variables = ImmutableConfigMap.of(Map.of("env", "prod"));

    assertThrows(NullPointerException.class, () ->
        new DefaultResourceLocation("classpath", null, resourceNames, variables, false,
            placeholder)
    );
  }

  @Test
  @DisplayName("should throw NullPointerException when resourceNames is null")
  void shouldThrowWhenResourceNamesIsNull() {
    var baseDirectories = ImmutableConfigSet.of(Set.of("config"));
    var variables = ImmutableConfigMap.of(Map.of("env", "prod"));

    assertThrows(NullPointerException.class, () ->
        new DefaultResourceLocation("classpath", baseDirectories, null, variables, false,
            placeholder)
    );
  }

  @Test
  @DisplayName("should throw NullPointerException when variables is null")
  void shouldThrowWhenVariablesIsNull() {
    var baseDirectories = ImmutableConfigSet.of(Set.of("config"));
    var resourceNames = ImmutableConfigSet.of(Set.of("config.yml"));

    assertThrows(NullPointerException.class, () ->
        new DefaultResourceLocation("classpath", baseDirectories, resourceNames, null, false,
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
        new DefaultResourceLocation("classpath", baseDirectories, resourceNames, variables,
            false, null)
    );
  }
}
