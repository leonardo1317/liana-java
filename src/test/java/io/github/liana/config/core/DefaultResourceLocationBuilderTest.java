package io.github.liana.config.core;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

import io.github.liana.config.api.ResourceLocation;
import io.github.liana.config.api.Placeholder;
import io.github.liana.config.api.ResourceLocationBuilder;
import io.github.liana.config.core.exception.InvalidVariablesException;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class DefaultResourceLocationBuilderTest {

  private DefaultResourceLocationBuilder builder;

  @BeforeEach
  void setUp() {
    builder = new DefaultResourceLocationBuilder();
  }

  @Test
  @DisplayName("should set provider and return same builder instance")
  void shouldSetProviderAndReturnSameBuilder() {
    ResourceLocationBuilder returned = builder.provider("classpath");

    assertSame(builder, returned);
  }

  @Test
  @DisplayName("should add multiple base directories from array and return same builder instance")
  void shouldAddMultipleBaseDirectoriesArray() {
    ResourceLocationBuilder returned = builder.baseDirectories("config", "app");

    assertSame(builder, returned);
  }

  @Test
  @DisplayName("should throw NullPointerException when base directories array is null")
  void shouldThrowWhenBaseDirectoriesArrayIsNull() {
    assertThrows(NullPointerException.class, () -> builder.baseDirectories((String[]) null));
  }

  @Test
  @DisplayName("should add single resource and return same builder instance")
  void shouldAddSingleResourceAndReturnSameBuilder() {
    ResourceLocationBuilder returned = builder.addResource("app.yaml");

    assertSame(builder, returned);
  }

  @Test
  @DisplayName("should add multiple resources from array and return same builder instance")
  void shouldAddMultipleResourcesFromArray() {
    ResourceLocationBuilder returned = builder.addResources("a.yaml", "b.json");

    assertSame(builder, returned);
  }

  @Test
  @DisplayName("should throw NullPointerException when addResources array is null")
  void shouldThrowWhenAddResourcesArrayIsNull() {
    assertThrows(NullPointerException.class, () -> builder.addResources((String[]) null));
  }

  @Test
  @DisplayName("should add resources from list and return same builder instance")
  void shouldAddResourcesFromList() {
    ResourceLocationBuilder returned = builder.addResourceFromList(
        List.of("conf.yaml", "conf.json"));

    assertSame(builder, returned);
  }

  @Test
  @DisplayName("should throw NullPointerException when addResourceFromList list is null")
  void shouldThrowWhenAddResourceFromListIsNull() {
    assertThrows(NullPointerException.class, () -> builder.addResourceFromList(null));
  }

  @Test
  @DisplayName("should add variable key-value pair and return same builder instance")
  void shouldAddVariableKeyValuePair() {
    ResourceLocationBuilder returned = builder.addVariable("env", "prod");

    assertSame(builder, returned);
  }

  @Test
  @DisplayName("should wrap IllegalArgumentException when variable key is invalid")
  void shouldWrapIllegalArgumentExceptionWhenVariableKeyIsInvalid() {
    builder.addVariable("env", "prod");
    assertThrows(InvalidVariablesException.class,
        () -> builder.addVariable("", "staging"));
  }

  @Test
  @DisplayName("should add variables from varargs and return same builder instance")
  void shouldAddVariablesFromVarargs() {
    ResourceLocationBuilder returned = builder.addVariables("env", "test", "region", "us");

    assertSame(builder, returned);
  }

  @Test
  @DisplayName("should throw NullPointerException when addVariables varargs is null")
  void shouldThrowWhenAddVariablesArrayIsNull() {
    assertThrows(NullPointerException.class, () -> builder.addVariables((String[]) null));
  }

  @Test
  @DisplayName("should wrap IllegalArgumentException when addVariables varargs key is invalid")
  void shouldWrapIllegalArgumentExceptionWhenVariablesKeyIsInvalid() {
    assertThrows(InvalidVariablesException.class, () -> builder.addVariables("", "staging"));
  }

  @Test
  @DisplayName("should throw InvalidVariablesException when map contains invalid key")
  void shouldThrowInvalidConfigVariablesWhenMapContainsInvalidKey() {
    Map<String, String> invalidMap = Map.of("", "someValue");

    assertThrows(InvalidVariablesException.class,
        () -> builder.addVariablesFromMap(invalidMap));
  }

  @Test
  @DisplayName("should add variables from map and return same builder instance")
  void shouldAddVariablesFromMap() {
    Map<String, String> map = Map.of("env", "dev");

    ResourceLocationBuilder returned = builder.addVariablesFromMap(map);

    assertSame(builder, returned);
  }

  @Test
  @DisplayName("should throw NullPointerException when addVariablesFromMap is null")
  void shouldThrowWhenAddVariablesFromMapIsNull() {
    assertThrows(NullPointerException.class, () -> builder.addVariablesFromMap(null));
  }

  @Test
  @DisplayName("should enable verbose logging and return same builder instance")
  void shouldEnableVerboseLoggingAndReturnSameBuilder() {
    ResourceLocationBuilder returned = builder.verboseLogging(true);

    assertSame(builder, returned);
  }

  @Test
  @DisplayName("should set placeholder and return same builder instance")
  void shouldSetPlaceholderAndReturnSameBuilder() {
    Placeholder placeholder = mock(Placeholder.class);

    ResourceLocationBuilder returned = builder.placeholders(placeholder);

    assertSame(builder, returned);
  }

  @Test
  @DisplayName("should throw NullPointerException when placeholder is null")
  void shouldThrowWhenPlaceholderIsNull() {
    assertThrows(NullPointerException.class, () -> builder.placeholders(null));
  }

  @Test
  @DisplayName("should build DefaultResourceLocation with default values when none set")
  void shouldBuildWithDefaultValuesWhenNoneSet() {
    ResourceLocation result = builder.build();

    assertNotNull(result);
  }

  @Test
  @DisplayName("should build DefaultResourceLocation with provided parameters")
  void shouldBuildWithProvidedParameters() {
    var placeholder = mock(Placeholder.class);
    builder
        .provider("classpath")
        .addResources("conf.yaml")
        .addVariable("env", "prod")
        .verboseLogging(true)
        .placeholders(placeholder);

    ResourceLocation result = builder.build();

    assertNotNull(result);
  }
}
