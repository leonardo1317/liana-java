<img src="liana-logo.png" alt="Liana Logo" width="200"/>

# Liana

**Liana** es una librería ligera que permite la configuración y uso de distintos tipos de clientes (REST, MongoDB, Kafka, SQL y más) sin acoplarse a ningún framework.  
Está pensada para ser **fácil de usar**, **extensible** y **completamente configurable** a través de archivos `.yml` o `.properties`.

> Como una liana, esta librería se adapta a múltiples entornos y crece sobre cualquier base.

---

## 🚀 Características

- Clientes configurables para:
  - REST
  - MongoDB
  - SQL
  - Kafka
  - Y más (en desarrollo)
- Compatible con Java puro o Spring Boot.
- Archivos de configuración en `.yml` o `.properties`.
- Fácil de extender con tus propios clientes.
- No requiere anotaciones ni clases especiales.

---

## 📦 Instalación

Disponible próximamente en Maven Central.

Mientras tanto, puedes instalarlo localmente:

```bash
./gradlew publishToMavenLocal
```

---

## 🛠️ Ejemplo de uso

```java
ConfigReader configReader = new DefaultConfigReader("config.properties");
RestClient restClient = new RestClientFactory(configReader).create();

String response = restClient.get("/api/hello");
System.out.println(response);
```

**config.properties:**
```properties
rest.base_url=https://api.example.com
rest.timeout=5000
```

---

## 📚 Estructura base

- `ConfigReader`: interfaz para acceder a configuración.
- `DefaultConfigReader`: implementación por defecto que lee `.properties` o `.yml`.
- `RestClientFactory`: construye el cliente REST a partir de la configuración.
- `RestClient`: cliente REST desacoplado.

---

## 🔧 Clientes disponibles

| Cliente  | Estado     |
|----------|------------|
| REST     | ✅ Estable |
| MongoDB  | 🚧 En desarrollo |
| SQL      | 🚧 En desarrollo |
| Kafka    | 🚧 En desarrollo |

---

## 🤝 Contribuciones

¡Son bienvenidas! Puedes abrir un issue o enviar un PR.

---

## 📜 Licencia

Apache License 2.0

---

## 🌿 Inspiración

Después de años trabajando en múltiples entornos (Spring, Jakarta EE, Java puro), decidí construir algo que pudiera adaptarse a todos —como una liana.