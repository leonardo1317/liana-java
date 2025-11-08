package io.github.liana.config;

public interface ThrowingSupplier<T> {
  T get() throws Exception;
}
