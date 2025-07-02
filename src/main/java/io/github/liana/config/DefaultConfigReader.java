package io.github.liana.config;

import static io.github.liana.internal.StringUtils.requireNonBlank;
import static java.util.Objects.requireNonNull;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

class DefaultConfigReader implements ConfigReader {

  private final Configuration config;

  public DefaultConfigReader(Configuration config) {
    this.config = requireNonNull(config, "Configuration must not be null");
  }

  @Override
  public Map<String, Object> getAllConfig() {
    return Collections.unmodifiableMap(config.getAllConfig());
  }

  @Override
  public <T> Optional<T> getAllConfigAs(Class<T> clazz) {
    requireNonNull(clazz, "clazz must not be null");
    return config.getAllConfigAs(clazz);
  }

  @Override
  public boolean hasKey(String key) {
    requireNonNull(key, "key must not be null");
    requireNonBlank(key, "key must not be blank");
    return config.hasKey(key);
  }

  @Override
  public <T> Optional<T> get(String key, Class<T> clazz) {
    validateKeyAndType(key, clazz);
    return config.get(key, clazz);
  }

  @Override
  public <T> Optional<T> get(String key, TypeOf<T> type) {
    requireNonNull(key, "key must not be null");
    requireNonBlank(key, "key must not be blank");
    requireNonNull(type, "type must not be null");
    return config.get(key, type.getType());
  }

  @Override
  public <E> List<E> getList(String key, Class<E> clazz, List<E> defaultValue) {
    validateKeyAndType(key, clazz);
    List<E> list = config.getList(key, clazz);
    return list.isEmpty() ? defaultValue : list;
  }

  @Override
  public <V> Map<String, V> getMap(String key, Class<V> clazz, Map<String, V> defaultValue) {
    validateKeyAndType(key, clazz);
    Map<String, V> map = config.getMap(key, clazz);
    return map.isEmpty() ? defaultValue : map;
  }

  private void validateKeyAndType(String key, Class<?> clazz) {
    requireNonNull(key, "key must not be null");
    requireNonBlank(key, "key must not be blank");
    requireNonNull(clazz, "clazz must not be null");
  }
}
