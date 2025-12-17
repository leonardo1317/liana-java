package io.github.liana.config.core;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ResourceStreamTest {

  @Test
  @DisplayName("should create ResourceStream and retrieve name and content")
  void shouldCreateResourceStream() throws IOException {
    String resourceName = "config.yaml";
    byte[] expectedContent = "name".getBytes(StandardCharsets.UTF_8);

    ByteArrayInputStream inputStream = new ByteArrayInputStream(expectedContent);

    try (ResourceStream resource = ResourceStream.from(resourceName, inputStream)) {
      assertEquals(resourceName, resource.name());
      InputStream stream = resource.stream();
      assertNotNull(stream);
      byte[] actualContent = stream.readAllBytes();
      assertArrayEquals(expectedContent, actualContent);
    }
  }

  @Test
  @DisplayName("should throw NullPointerException when name is null")
  void shouldThrowWhenNameIsNull() {
    InputStream inputStream = new ByteArrayInputStream(new byte[0]);
    assertThrows(NullPointerException.class, () -> ResourceStream.from(null, inputStream));
  }

  @Test
  @DisplayName("should throw NullPointerException when stream is null")
  void shouldThrowWhenStreamIsNull() {
    assertThrows(NullPointerException.class, () -> ResourceStream.from("file.txt", null));
  }
}
