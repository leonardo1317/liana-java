package io.github.liana.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class LoadingCacheTest {

  private LoadingCache<String, String> cache;

  @BeforeEach
  void setUp() {
    cache = new LoadingCache<>();
  }

  @Test
  @DisplayName("should compute and cache value when key is not present")
  void shouldComputeAndCacheValueWhenKeyNotPresent() {
    Supplier<String> loader = () -> "computed";

    String result = cache.getOrCompute("key", loader);

    assertEquals("computed", result);
  }

  @Test
  @DisplayName("should return cached value when key is already present")
  void shouldReturnCachedValueWhenKeyAlreadyPresent() {
    AtomicInteger computeCount = new AtomicInteger();

    Supplier<String> loader = () -> {
      computeCount.incrementAndGet();
      return "cached";
    };

    String firstCall = cache.getOrCompute("key", loader);
    String secondCall = cache.getOrCompute("key", loader);

    assertEquals("cached", firstCall);
    assertEquals("cached", secondCall);
    assertEquals(1, computeCount.get());
  }

  @Test
  @DisplayName("should throw NullPointerException when key is null")
  void shouldThrowExceptionWhenKeyIsNull() {
    assertThrows(NullPointerException.class, () -> cache.getOrCompute(null, () -> "value"));
  }

  @Test
  @DisplayName("should throw NullPointerException when loader is null")
  void shouldThrowExceptionWhenLoaderIsNull() {
    assertThrows(NullPointerException.class, () -> cache.getOrCompute("key", null));
  }

  @Test
  @DisplayName("should handle concurrent access without duplicate computation")
  void shouldHandleConcurrentAccessWithoutDuplicateComputation() throws InterruptedException {
    AtomicInteger computeCount = new AtomicInteger();
    Supplier<String> loader = () -> {
      computeCount.incrementAndGet();
      return "value";
    };

    Runnable task = () -> cache.getOrCompute("shared-key", loader);
    Thread t1 = new Thread(task);
    Thread t2 = new Thread(task);

    t1.start();
    t2.start();
    t1.join();
    t2.join();

    assertEquals(1, computeCount.get());
    assertEquals("value", cache.getOrCompute("shared-key", loader));
  }
}
