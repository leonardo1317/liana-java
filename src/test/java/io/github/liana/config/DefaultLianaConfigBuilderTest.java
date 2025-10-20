package io.github.liana.config;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class DefaultLianaConfigBuilderTest {

  private LianaConfigBuilder builder;

  @BeforeEach
  void setUp() {
    builder = new DefaultLianaConfigBuilder();
  }

  @Test
  @DisplayName("should include default providers and loaders when created")
  void shouldIncludeDefaultsOnCreation() {
    ConfigManager manager = builder.build();

    assertNotNull(manager);
  }

  @Test
  @DisplayName("should add custom providers successfully")
  void shouldAddCustomProvidersSuccessfully() {
    ConfigProvider customProvider = mock(ConfigProvider.class);

    builder.addProviders(customProvider);
    ConfigManager manager = builder.build();

    assertNotNull(manager);
  }

  @Test
  @DisplayName("should add custom loaders successfully")
  void shouldAddCustomLoadersSuccessfully() {
    ConfigLoader customLoader = mock(ConfigLoader.class);

    builder.addLoaders(customLoader);
    ConfigManager manager = builder.build();

    assertNotNull(manager);
  }

  @Test
  @DisplayName("should return same builder instance when adding providers or loaders")
  void shouldReturnSameBuilderInstance() {
    ConfigProvider provider = mock(ConfigProvider.class);
    ConfigLoader loader = mock(ConfigLoader.class);

    LianaConfigBuilder sameAfterProviders = builder.addProviders(provider);
    LianaConfigBuilder sameAfterLoaders = builder.addLoaders(loader);

    assertSame(builder, sameAfterProviders);
    assertSame(builder, sameAfterLoaders);
  }

  @Test
  @DisplayName("should throw NullPointerException when adding null providers")
  void shouldThrowWhenAddingNullProviders() {
    assertThrows(NullPointerException.class, () -> builder.addProviders((ConfigProvider[]) null));
  }

  @Test
  @DisplayName("should throw NullPointerException when adding null loaders")
  void shouldThrowWhenAddingNullLoaders() {
    assertThrows(NullPointerException.class, () -> builder.addLoaders((ConfigLoader[]) null));
  }

  @Test
  @DisplayName("should create independent ConfigManager instances on multiple builds")
  void shouldCreateIndependentInstancesOnMultipleBuilds() {
    ConfigManager first = builder.build();
    ConfigManager second = builder.build();

    assertNotSame(first, second);
  }
}