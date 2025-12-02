package io.github.liana.config.core;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

import io.github.liana.config.api.ConfigurationManager;
import io.github.liana.config.api.ConfigurationManagerBuilder;
import io.github.liana.config.spi.ResourceLoader;
import io.github.liana.config.spi.ResourceProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class DefaultConfigurationManagerBuilderTest {

  private ConfigurationManagerBuilder builder;

  @BeforeEach
  void setUp() {
    builder = new DefaultConfigurationManagerBuilder();
  }

  @Test
  @DisplayName("should include default providers and loaders when created")
  void shouldIncludeDefaultsOnCreation() {
    ConfigurationManager manager = builder.build();

    assertNotNull(manager);
  }

  @Test
  @DisplayName("should add custom providers successfully")
  void shouldAddCustomProvidersSuccessfully() {
    ResourceProvider customProvider = mock(ResourceProvider.class);

    builder.addProviders(customProvider);
    ConfigurationManager manager = builder.build();

    assertNotNull(manager);
  }

  @Test
  @DisplayName("should add custom loaders successfully")
  void shouldAddCustomLoadersSuccessfully() {
    ResourceLoader customLoader = mock(ResourceLoader.class);

    builder.addLoaders(customLoader);
    ConfigurationManager manager = builder.build();

    assertNotNull(manager);
  }

  @Test
  @DisplayName("should return same builder instance when adding providers or loaders")
  void shouldReturnSameBuilderInstance() {
    ResourceProvider provider = mock(ResourceProvider.class);
    ResourceLoader loader = mock(ResourceLoader.class);

    ConfigurationManagerBuilder sameAfterProviders = builder.addProviders(provider);
    ConfigurationManagerBuilder sameAfterLoaders = builder.addLoaders(loader);

    assertSame(builder, sameAfterProviders);
    assertSame(builder, sameAfterLoaders);
  }

  @Test
  @DisplayName("should throw NullPointerException when adding null providers")
  void shouldThrowWhenAddingNullProviders() {
    assertThrows(NullPointerException.class, () -> builder.addProviders((ResourceProvider[]) null));
  }

  @Test
  @DisplayName("should throw NullPointerException when adding null loaders")
  void shouldThrowWhenAddingNullLoaders() {
    assertThrows(NullPointerException.class, () -> builder.addLoaders((ResourceLoader[]) null));
  }

  @Test
  @DisplayName("should create independent ConfigurationManager instances on multiple builds")
  void shouldCreateIndependentInstancesOnMultipleBuilds() {
    ConfigurationManager first = builder.build();
    ConfigurationManager second = builder.build();

    assertNotSame(first, second);
  }
}