## Advanced Usage and API Reference

### Define your configuration source:

```java
ResourceLocation location = ResourceLocation.builder()
    .provider("classpath") // Optional – if omitted, "classpath" is used as the default provider
    .baseDirectories("", "config")
    .addResources(
        "application.properties",              // loaded via provider ("classpath")
        "application-${profile}.properties",   // resolved using variables
        "classpath:application.json",          // explicit provider overrides global
        "file:application.xml"                 // explicit file provider
    )
    .addVariables(
        "profile", "dev"
    )
    .verboseLogging(true) // Optional – default is false
    .placeholders(        // Optional – default is ${var:default}
        Placeholder.builder()
            .prefix("${")
            .suffix("}")
            .delimiter(":")
            .build()
    )
    .build();
```

### Example configuration files (per format):

**Properties:**

```properties
app.name=Liana
servers[0].host=localhost
servers[0].port=8080
servers[1].host=example.com
servers[1].port=9090
```

**YAML:**

```yaml
app:
  name: Liana
servers:
  - host: "localhost"
    port: 8080
  - host: "example.com"
    port: 9090
```

**JSON:**

```json
{
  "app": {
    "name": "Liana"
  },
  "servers": [
    {
      "host": "localhost",
      "port": 8080
    },
    {
      "host": "example.com",
      "port": 9090
    }
  ]
}
```

**XML:**

```xml

<config>
  <app>
    <name>Liana</name>
  </app>
  <servers>
    <server>
      <host>localhost</host>
      <port>8080</port>
    </server>
    <server>
      <host>example.com</host>
      <port>9090</port>
    </server>
  </servers>
</config>
```

### Example POJO classes:

```java
public record AppConfig(String name) {

}

public record ServerConfig(String host, int port) {

}
```

### Load and read configuration:

```java
ConfigurationManager manager = ConfigurationManager.builder().build();
Configuration configuration = manager.load(location);
String appName = configuration.getString("app.name", "DefaultApp");
int port = configuration.getInt("server.port", 8080);
```

### Load as POJO:

```java
AppConfig config = configuration.get("app", AppConfig.class, new AppConfig());
List<ServerConfig> servers = configuration.get("servers", new TypeOf<List<ServerConfig>>() {
}, List.of());
```

### Optional Variants:

```java
Optional<String> optionalAppName = configuration.get("app.name", String.class);
Optional<Integer> optionalPort = configuration.get("server.port", Integer.class);

Optional<AppConfig> optionalConfig = configuration.get("app", AppConfig.class);
Optional<List<ServerConfig>> optionalServers = configuration.get("servers",
    new TypeOf<List<ServerConfig>>() {
    });
```

## ConfigurationManager API

The following table documents all available methods in the `ConfigurationManager` API:

| Method                                          | Description                                                                    | Example                                  |
|-------------------------------------------------|--------------------------------------------------------------------------------|------------------------------------------|
| `Configuration load(ResourceLocation location)` | Loads a configuration resource described by a ResourceLocation.                | `manager.load(location)`                 |
| `static ConfigurationManagerBuilder builder()`  | Creates a new builder for constructing a custom ConfigurationManager instance. | `ConfigurationManager.builder().build()` |

## ConfigurationManagerBuilder API

The following table documents all available methods in the `ConfigurationManagerBuilder` API:

| Method                                                                    | Description                                                                                                               | Example                                                        |
|---------------------------------------------------------------------------|---------------------------------------------------------------------------------------------------------------------------|----------------------------------------------------------------|
| `ConfigurationManagerBuilder addProviders(ResourceProvider... providers)` | Registers one or more ResourceProvider implementations. Providers resolve logical names into physical resources.          | `.addProviders(new ClasspathProvider(), new CustomProvider())` |
| `ConfigurationManagerBuilder addLoaders(ResourceLoader... loaders)`       | Registers one or more ResourceLoader implementations. Loaders convert resolved resources into parsed configuration trees. | `.addLoaders(new JsonLoader(), new CustomLoader())`            |
| `ConfigurationManager build()`                                            | Constructs a fully configured ConfigurationManager applying defaults for any unconfigured components.                     | `.build()`                                                     |

## ResourceLocation API

The following table documents all available methods in the `ResourceLocation` API:

| Method                                     | Description                                                                                                                            | Example                                   |
|--------------------------------------------|----------------------------------------------------------------------------------------------------------------------------------------|-------------------------------------------|
| `String provider()`                        | Returns the provider identifier responsible for loading resources (e.g., "classpath").                                                 | `"classpath"` *(optional)*                |
| `ImmutableConfigSet baseDirectories()`     | Returns the ordered set of base directories where the provider should search for resources. Order affects lookup and merge precedence. | `["config", "defaults"]`                  |
| `ImmutableConfigSet resourceNames()`       | Returns the logical resource names to load. Each logical name may resolve to multiple physical files depending on the provider.        | `["app.yaml", "application.json"]`        |
| `ImmutableConfigMap variables()`           | Returns the variable bindings used for placeholder interpolation when loading resources.                                               | `{ "env": "dev", "region": "us-east-1" }` |
| `boolean verboseLogging()`                 | Indicates whether verbose logging should be enabled during resource resolution. Useful for debugging provider and loader behavior.     | `true`                                    |
| `Placeholder placeholder()`                | Returns the placeholder strategy to use when resolving variable expressions inside resources.                                          | `Placeholder`                             |
| `static ResourceLocationBuilder builder()` | Returns a new builder for creating a ResourceLocation instance.                                                                        | `ResourceLocation.builder().build()`      |

## ResourceLocationBuilder API

The following table documents all available methods in the `ResourceLocationBuilder` API:

| Method                                                                      | Description                                                                                                                      | Example                                                                  |
|-----------------------------------------------------------------------------|----------------------------------------------------------------------------------------------------------------------------------|--------------------------------------------------------------------------|
| `ResourceLocationBuilder provider(String provider)`                         | Sets the provider that will be used for resources without an explicit provider prefix. Defaults to "classpath" if not specified. | `.provider("classpath")`                                                 |
| `ResourceLocationBuilder baseDirectories(String... baseDirectories)`        | Defines the base directories where providers will search for files. Order determines precedence.                                 | `.baseDirectories("", "config")`                                         |
| `ResourceLocationBuilder addResource(String resourceName)`                  | Adds a single resource name to resolve.                                                                                          | `.addResource("application.properties")`                                 |
| `ResourceLocationBuilder addResources(String... resources)`                 | Adds multiple resource names for resolution.                                                                                     | `.addResources("app.json", "app.xml")`                                   |
| `ResourceLocationBuilder addResourceFromList(List<String> resources)`       | Adds a list of resource names. Useful when names come from external collections.                                                 | `.addResourceFromList(List.of("a.yml", "b.yml"))`                        |
| `ResourceLocationBuilder addVariable(String key, String value)`             | Adds a single variable for placeholder interpolation. Throws InvalidVariablesException for invalid pairs.                        | `.addVariable("profile", "dev")`                                         |
| `ResourceLocationBuilder addVariables(String... variables)`                 | Adds variables using alternating key/value pairs.                                                                                | `.addVariables("profile", "dev", "region", "us-east-1")`                 |
| `ResourceLocationBuilder addVariablesFromMap(Map<String,String> variables)` | Adds variables from a map.                                                                                                       | `.addVariablesFromMap(Map.of("env","prod"))`                             |
| `ResourceLocationBuilder verboseLogging(boolean verboseLogging)`            | Enables or disables detailed logging during resource resolution. Off by default.                                                 | `.verboseLogging(true)`                                                  |
| `ResourceLocationBuilder placeholders(Placeholder placeholder)`             | Sets the placeholder engine to use. If not specified, a default engine with prefix "${"}, delimiter ":", suffix "}" is applied.  | `.placeholders(Placeholder.builder().prefix("{{").suffix("}}").build())` |
| `ResourceLocation build()`                                                  | Builds an immutable ResourceLocation. Applies defaults for provider and placeholders if missing.                                 | `.build()`                                                               |

## Placeholder API

The following table documents all available methods in the `Placeholder` API:

| Method                                                                                      | Description                                                                                                                                                                | Example                                                                                |
|---------------------------------------------------------------------------------------------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------------|----------------------------------------------------------------------------------------|
| `Optional<String> replaceIfAllResolvable(String template, PropertySource... extraSources)`  | Resolves all placeholders in the template. Returns an empty Optional if any placeholder cannot be resolved. Uses PropertySource instances plus any extra sources provided. | `replaceIfAllResolvable("Hello ${name}", propertySource);`                             |
| `Optional<String> replaceIfAllResolvable(String template, Map<String, String> extraValues)` | Convenience overload that wraps the provided map as a temporary PropertySource. Behaves exactly like the other method.                                                     | `replaceIfAllResolvable("URL: ${host:localhost}", Map.of("host","prod.example.com"));` |
| `static PlaceholderBuilder builder()`                                                       | Creates a new PlaceholderBuilder with default syntax (${, }, :).                                                                                                           | `builder().build();`                                                                   |

## PlaceholderBuilder API

The following table documents all available methods in the `PlaceholderBuilder` API:

| Method                                            | Description                                                                          | Example            |
|---------------------------------------------------|--------------------------------------------------------------------------------------|--------------------|
| `PlaceholderBuilder prefix(String prefix)`        | Sets the prefix that marks the beginning of a placeholder expression. Default: "${". | `.prefix("{{")`    |
| `PlaceholderBuilder suffix(String suffix)`        | Sets the suffix that ends a placeholder expression. Default: "}".                    | `.suffix("}}")`    |
| `PlaceholderBuilder delimiter(String delimiter)`  | Sets the delimiter between key and default value. Default: ":".                      | `.delimiter(";")`  |
| `PlaceholderBuilder escapeChar(char escapeChar))` | Defines the escape character to prevent resolution. Default: '\\'.                   | `.escapeChar('!')` |
| `Placeholder build()`                             | Builds an immutable Placeholder instance using the configured syntax.                | `.build()`         |

## ResourceProvider API

The following table documents all available methods in the `ResourceProvider` API:

| Method                                                        | Description                                                                                                                                                                         | Example                                |
|---------------------------------------------------------------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|----------------------------------------|
| `ResourceStream resolveResource(ResourceIdentifier resource)` | Resolves a logical resource identifier into a physical, loadable ResourceStream. Implementations define how resources are retrieved (filesystem, classpath, HTTP, in-memory, etc.). | `provider.resolveResource(resource);`  |
| `default void validateResource(ResourceIdentifier resource)`  | Performs basic validation of the resource identifier. Implementations may override to add stricter validation rules.                                                                | `provider.validateResource(resource);` |
| `Set<String> keys() (from Strategy<String>)`                  | Returns the unique provider identifiers (e.g., "classpath", "file", "http"). These keys are used by the resolution pipeline.                                                        | `provider.keys()`                      |

## ResourceLoader API

The following table documents all available methods in the `ResourceLoader` API:

| Method                                                   | Description                                                                                                                      | Example                            |
|----------------------------------------------------------|----------------------------------------------------------------------------------------------------------------------------------|------------------------------------|
| `Configuration load(ResourceStream resource)`            | Loads and parses configuration data from the given resource. Implementations define how each format is parsed (JSON, TOML, etc.. | `loader.load(stream);`             |
| `default void validateResource(ResourceStream resource)` | Ensures that the stream and metadata are not null. Implementations may extend with stricter validation.                          | `loader.validateResource(stream);` |
| `Set<String> keys() (from Strategy<String>)`             | Returns the file extensions supported by the loader (e.g., "json", "properties", "xml").                                         | `loader.keys();`                   |

## Configuration API

The following table documents all available methods in the `Configuration` API:

| Method                                                    | Description                                                   | Example                                                         |
|-----------------------------------------------------------|---------------------------------------------------------------|-----------------------------------------------------------------|
| `boolean containsKey(String key)`                         | Checks whether the configuration contains the given key.      | `config.containsKey("app.port");`                               |
| `<T> Optional<T> get(String key, Class<T> clazz)`         | Retrieves a value and converts it to the specified class.     | `config.get("app.name", String.class);`                         |
| `<T> Optional<T> get(String key, TypeOf<T> type)`         | Retrieves a value using a generic type (e.g., lists or maps). | `config.get("servers", new TypeOf<List<String>>(){});`          |
| `<T> T get(String key, Class<T> clazz, T defaultValue)`   | Returns the value or a default when missing.                  | `config.get("app.port", Integer.class, 8080);`                  |
| `<T> T get(String key, TypeOf<T> type, T defaultValue)`   | Generic-type version of get with default fallback.            | `config.get("tags", new TypeOf<List<String>>() {}, List.of());` |
| `<T> T getOrThrow(String key, Class<T> clazz)`            | Retrieves a required value, throws if absent.                 | `config.getOrThrow("db.url", String.class);`                    |
| `<T> T getOrThrow(String key, TypeOf<T> type`             | Required-read version for generic types.                      | `config.getOrThrow("metadata", new TypeOf<>(){});`              |
| `String getString(String key)`                            | Retrieves a required string.                                  | `config.getString("env.mode");`                                 |
| `int getInt(String key)`                                  | Retrieves a required integer.                                 | `config.getInt("threads");`                                     |
| `boolean getBoolean(String key)`                          | Retrieves a required boolean.                                 | `config.getBoolean("debug.enabled");`                           |
| `double getDouble(String key)`                            | Retrieves a required double.                                  | `config.getDouble("balance.ratio");`                            |
| `Duration getDuration(String key)`                        | Retrieves a required duration.                                | `config.getDuration("http.timeout");`                           |
| `String getString(String key, String defaultValue)`       | Retrieves a string or uses default.                           | `config.getString("db.host", "localhost");`                     |
| `int getInt(String key, int defaultValue)`                | Retrieves an int or uses default.                             | `config.getInt("retries", 3);`                                  |
| `boolean getBoolean(String key, boolean defaultValue)`    | Retrieves a boolean or uses default.                          | `config.getBoolean("cache.enabled", true);`                     |
| `double getDouble(String key, double defaultValue)`       | Retrieves a double or uses default.                           | `config.getDouble("limit", 0.5);`                               |
| `Duration getDuration(String key, Duration defaultValue)` | Retrieves a duration or uses default.                         | `config.getDuration("delay", Duration.ofSeconds(5));`           |
| `<E> List<E> getList(String key, Class<E> clazz)`         | Returns a list of converted elements.                         | `config.getList("servers", String.class);`                      |
| `<V> Map<String, V> getMap(String key, Class<V> clazz)`   | Returns a nested map converted to the target type.            | `config.getMap("limits", Integer.class);`                       |
| `Map<String, Object> getRootAsMap()`                      | Returns the entire configuration as an unmodifiable map.      | `config.getRootAsMap();`                                        |
| `<T> Optional<T> getRootAs(Type type)`                    | Converts the root into a target type (POJO, map, list, etc.). | `config.getRootAs(AppConfig.class).orElseThrow();`              |
