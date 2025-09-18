![Liana](banner-github-clean.png)

# Liana Config

&#x20;

---

## Overview

**Liana** is a lightweight, framework agnostic Java configuration library designed for simplicity and flexibility. Inspired by the liana plant that adapts to any structure, Liana adapts to your application's needs not the other way around.

Liana abstracts configuration complexity and offers a unified API to load configurations from [supported formats](#supported-formats) without forcing the use of heavyweight frameworks.

---

## Philosophy

> "Tell me where your configuration files are, and I'll handle the rest."

Liana prioritizes:

- ⚡ **Minimalism**: No forced conventions or complex setups.
- 🔄 **Adaptability**: Works with supported formats and file structures.
- 🪶 **Simplicity**: One-time load with cache for fast repeated access.
- 🔍 **Clarity**: Supports placeholders, overrides, and variables.

---

## Supported Formats

- **Properties**
- **YAML**
- **JSON**
- **XML**

---

## Key Features

Liana provides essential configuration capabilities designed for flexibility and simplicity in Java applications:

- **Multi-format support**: Load and merge multiple configuration files in [supported formats](#supported-formats).
- **Ordered overrides**: Later-loaded files override earlier ones for environment-specific layering.
- **Custom placeholder resolution**: Replace placeholders (`${profile}` or `${profile:default}`) dynamically using variable maps.
- **Deep interpolation**: Automatically interpolates placeholder variables across all textual nodes in nested structures.
- **Variable injection**: Inject variables via fluent API or programmatically.
- **Type-safe access**: Retrieve config as `String`, `int`, `boolean`, lists, maps, arrays, or POJOs.
- **POJO and generic mapping**: Deserialize config sections into POJOs or generic structures using `TypeOf<T>`. POJO fields should be private with public getters and setters.
- **Complete config snapshot**: Access the full config tree as an unmodifiable `Map<String, Object>` or a full POJO.
- **Thread-safe and immutable**: Config data is immutable after loading.
- **Optional verbose logging**: Detailed logs for resource loading and resolution.
- **Strict file validation**: Validates resource names against security and compatibility rules before loading.

---

## Installation

**Maven:**

```xml
<dependency>
  <groupId>io.github.liana</groupId>
  <artifactId>liana-config</artifactId>
  <version>0.1.0</version>
</dependency>
```

**Gradle:**

```groovy
dependencies {
  implementation 'io.github.liana:liana-config:0.1.0'
}
```

---

## Quick Start

### Define your configuration source:

```java
ConfigResourceLocation location = ConfigResourceLocations.builder()
    .addResources("application.yaml", "application-${profile}.yaml")
    .addVariables("profile", "dev")
    .verboseLogging(true) // optional, default is false. Enables detailed logs of the loading process.
    .build();
```
If you define the configuration like this:

```java
ConfigResourceLocation location = ConfigResourceLocations.builder().build();
```

Liana applies the following **defaults**:

- Provider: **classpath**
- Profile variable: **profile**
- Default profile: **default**
- Profile environment variable: **LIANA_PROFILE**
- Base resource name: **application**
- Base resource pattern: **application-${profile}**

This means Liana will search the classpath for:

1. A file named `application` (in any [supported format](#supported-formats)).
2. A file matching the pattern `application-${profile}` (with `${profile}` resolved from the environment variable `LIANA_PROFILE`).

If `LIANA_PROFILE` is **not set**, Liana uses the default profile value: **default**.

The placeholder system supports the `${key}` and `${key:default}` syntax. Placeholders can be resolved using variables provided through `.addVariables(...)` or `.addVariablesFromMap(...)`, and placeholders inside values are deeply interpolated across the full configuration structure.

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
    { "host": "localhost", "port": 8080 },
    { "host": "example.com", "port": 9090 }
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
public class AppConfig {
  private String name;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }
}

public class ServerConfig {
  private String host;
  private int port;

  public String getHost() {
    return host;
  }

  public void setHost(String host) {
    this.host = host;
  }

  public int getPort() {
    return port;
  }

  public void setPort(int port) {
    this.port = port;
  }
}
```

### Load and read configuration:

```java
ConfigReader reader = LianaConfig.getInstance().load(location);
String appName = reader.getString("app.name", "DefaultApp");
int port = reader.getInt("server.port", 8080);
```

### Load as POJO:

```java
AppConfig config = reader.get("app", AppConfig.class, new AppConfig());
List<ServerConfig> servers = reader.get("servers", new TypeOf<List<ServerConfig>>() {}, List.of());
```

### Optional Variants:

```java
Optional<String> optionalAppName = reader.get("app.name", String.class);
Optional<Integer> optionalPort = reader.get("server.port", Integer.class);

Optional<AppConfig> optionalConfig = reader.get("app", AppConfig.class);
Optional<List<ServerConfig>> optionalServers = reader.get("servers", new TypeOf<List<ServerConfig>>() {});
```

---

## ConfigResourceLocation API

### Strict File Validation

Liana enforces strict validation for file names to ensure security and compatibility across [supported formats](#supported-formats).

### Allowed File Name Rules

| Rule                                                          | Description                                                    |
| ------------------------------------------------------------- | -------------------------------------------------------------- |
| Must not be blank                                             | Prevents empty names                                           |
| Must not contain `..` or `../`                                | Prevents directory traversal                                   |
| Must not start with `..`                                      | Ensures safe directory targeting                               |
| Must normalize to a valid path                                | Path must resolve without invalid segments                     |
| Must end with a valid extension from [supported formats](#supported-formats) | Enforces compatible configuration file types    |

### Builder Methods

| Method                          | Description                                                                                   | Example                                              |
| ------------------------------- | --------------------------------------------------------------------------------------------- | ---------------------------------------------------- |
| `provider(String)`              | Sets the resource provider (e.g., "classpath"). **Defaults to "classpath" if not specified.** | `.provider("classpath")` *(optional)*                |
| `addResource(String)`           | Adds a single resource file to be loaded.                                                     | `.addResource("app.yaml")`                           |
| `addResources(String...)`       | Adds multiple resource files.                                                                 | `.addResources("app.yaml", "db.yaml")`               |
| `addVariables(String...)`       | Adds substitution variables using key-value pairs.                                            | `.addVariables("profile", "dev")`                    |
| `addVariablesFromMap(Map)`      | Adds substitution variables from a `Map<String, String>`.                                     | `.addVariablesFromMap(Map.of("profile", "prod"))`    |
| `verboseLogging(boolean)`       | Enables or disables verbose logging for detailed load process output.                         | `.verboseLogging(true)`                              |

---

## ConfigReader API

The following table documents all available methods in the `ConfigReader` API:

| Method                                               | Description                                                           | Example                                                                      |
| ---------------------------------------------------- |-----------------------------------------------------------------------| ---------------------------------------------------------------------------- |
| `get(String key, Class<T> type)`                     | Retrieves optional value for key, converted to specified type.        | `reader.get("app.name", String.class)`                                       |
| `get(String key, TypeOf<T> typeOf)`                  | Retrieves optional value for key, converted to complex/generic type.  | `reader.get("servers", new TypeOf<List<String>>() {})`                       |
| `get(String key, Class<T> type, T defaultValue)`     | Retrieves value or returns default if missing.                        | `reader.get("timeout", Integer.class, 30)`                                   |
| `get(String key, TypeOf<T> typeOf, T defaultValue)`  | Retrieves value for key (generic type) or returns default if missing. | `reader.get("servers", new TypeOf<List<String>>() {}, List.of("localhost"))` |
| `getString(String key)`                              | Retrieves string value.                                               | `reader.getString("app.name")`                                               |
| `getString(String key, String defaultValue)`         | Retrieves string or returns default.                                  | `reader.getString("app.name", "Default")`                                    |
| `getInt(String key)`                                 | Retrieves integer value.                                              | `reader.getInt("port")`                                                      |
| `getInt(String key, int defaultValue)`               | Retrieves integer or returns default.                                 | `reader.getInt("port", 8080)`                                                |
| `getBoolean(String key)`                             | Retrieves boolean value.                                              | `reader.getBoolean("enabled")`                                               |
| `getBoolean(String key, boolean defaultValue)`       | Retrieves boolean or returns default.                                 | `reader.getBoolean("enabled", false)`                                        |
| `getFloat(String key)`                               | Retrieves float value.                                                | `reader.getFloat("piValue")`                                                 |
| `getFloat(String key, float defaultValue)`           | Retrieves float or returns default.                                   | `reader.getFloat("piValue", 3.14f)`                                          |
| `getDouble(String key)`                              | Retrieves double value.                                               | `reader.getDouble("piValue")`                                                |
| `getDouble(String key, double defaultValue)`         | Retrieves double or returns default.                                  | `reader.getDouble("piValue", 3.1415)`                                        |
| `getStringArray(String key)`                         | Retrieves string array.                                               | `reader.getStringArray("hosts")`                                             |
| `getStringArray(String key, String[] defaultValue)`  | Retrieves string array or returns default.                            | `reader.getStringArray("hosts", new String[]{"localhost"})`                  |
| `getStringList(String key)`                          | Retrieves list of strings.                                            | `reader.getStringList("hosts")`                                              |
| `getStringList(String key, List<String> defaultVal)` | Retrieves list of strings or default.                                 | `reader.getStringList("hosts", List.of("localhost"))`                        |
| `getList(String key, Class<E> clazz)`                | Retrieves list of values of specified type.                           | `reader.getList("ports", Integer.class)`                                     |
| `getList(String key, Class<E> clazz, List<E> def)`   | Retrieves list of values or returns default.                          | `reader.getList("ports", Integer.class, List.of(80, 443))`                   |
| `getMap(String key, Class<V> clazz)`                 | Retrieves map of values of specified type.                            | `reader.getMap("db.settings", String.class)`                                 |
| `getMap(String key, Class<V> clazz, Map<V> def)`     | Retrieves map of values or returns default.                           | `reader.getMap("db.settings", String.class, Map.of("timeout", "30"))`        |
| `hasKey(String key)`                                 | Checks if key exists.                                                 | `reader.hasKey("app.name")`                                                  |
| `getAllConfig()`                                     | Retrieves all configuration as a Map.                                 | `reader.getAllConfig()`                                                      |
| `getAllConfigAs(Class<T> type)`                      | Converts the entire configuration to specified type.                  | `reader.getAllConfigAs(AppConfig.class)`                                     |

---

## Exceptions

| Method                                       | Exception                                     | Description                                               |
| -------------------------------------------- | --------------------------------------------- | --------------------------------------------------------- |
| `ConfigFactory.load()`                       | `ConfigException`, `IllegalArgumentException` | Thrown if resource location is invalid or loading fails.  |
| `ConfigReader.getOrThrow(String, Class)`     | `MissingConfigException`                      | Thrown when the requested key does not exist.             |
| `ConfigReader.getOrThrow(String, TypeOf)`    | `MissingConfigException`                      | Thrown when the requested key does not exist.             |
| `ConfigReader.getAllConfigAs(Class)`         | `NullPointerException`                        | Thrown if provided class is null.                         |
| `ConfigReader.get(String, Class)`            | `NullPointerException`, `IllegalArgumentException` | Thrown if key is null/blank or class is null.              |
| `ConfigReader.get(String, TypeOf)`           | `NullPointerException`, `IllegalArgumentException` | Thrown if key or TypeOf is null/blank.                    |
| `ConfigReader.getList()` / `getMap()`        | `NullPointerException`, `IllegalArgumentException` | Thrown if key is null/blank or class is null.              |
| `ConfigReader.hasKey(String)`                | `NullPointerException`, `IllegalArgumentException` | Thrown if key is null or blank.                           |
| All "required key" getters (without default) | `MissingConfigException`                      | Thrown if key does not exist and no default provided.     |

---

## Logging Example

```plaintext
Configuration load completed: loaded=2, failed=1 (total=3)
Loaded: application.yaml, application-dev.yaml
Failed: missing-config.yaml (not found)
```

---

## Contributing

We welcome contributions! Please see [CONTRIBUTING.md](CONTRIBUTING.md) for guidelines.

---

## License

Apache License 2.0

---

## Author

Leonardo R.

---

> "Liana: Configuration that adapts to you, not the other way around."
