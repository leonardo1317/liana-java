<img src="docs/latest/assets/images/banner.png" alt="Liana" width="380">

# Liana Config

**Liana** is a lightweight, framework-agnostic Java configuration library that adapts to your
application — not the other way around.

It provides a simple and predictable way to load, merge, and access configuration from multiple
sources and formats, without coupling your code to a specific framework or runtime.

## Why Liana?

Most configuration libraries are tightly coupled to frameworks or impose rigid conventions.

Liana focuses on:

- **Flexibility** over magic
- **Explicit behavior** over hidden defaults
- **Extensibility** without complexity

Use Liana when you want full control over how configuration is loaded, merged, and resolved — while
keeping your code clean and type-safe.

## Key Features

- **Multi-format support**
  Load configuration from Properties, YAML, JSON, and XML.
- **Ordered overrides**
  Later-loaded resources override earlier ones, ideal for environment-based layering.
- **Deep placeholder interpolation**
  Resolve ${var} and ${var:default} across nested configuration structures.
- **Type-safe access**
  Retrieve values as primitives, collections, maps, or map directly to POJOs.
- **Thread-safe and immutable**
  Configuration is immutable after loading and safe to share across threads.
- **Extensible by design**
  Add custom providers and loaders to support new formats or sources.

## Quick Start

Liana works out of the box with sensible defaults.

```java
ConfigurationManager manager = ConfigurationManager.builder().build();
ResourceLocation location = ResourceLocation.builder().build();
Configuration config = manager.load(location);

String appName = config.getString("app.name", "DefaultApp");
int port = config.getInt("server.port", 8080);
```

### Default behavior

By default, Liana:

- Uses the **classpath** as the configuration source
- Looks for files named `application` in supported formats
- Applies profile-based overrides using `application-${profile}`
- Resolves `${profile}` from the `LIANA_PROFILE` environment variable
- Falls back to the `default` profile when none is provided

## Installation

_Not available yet (coming soon)._

## Documentation

- **Full documentation**: [Liana documentation](https://leonardo1317.github.io/liana-config)

## Contributing

Contributions, issues, and ideas are welcome.

See [CONTRIBUTING.md](CONTRIBUTING.md) for guidelines.

## License

Apache License 2.0

## Author

Leonardo R.

> "Liana: Configuration that adapts to you, not the other way around."
