package io.github.liana.config;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.TextNode;
import io.github.liana.config.exception.ConversionException;
import io.github.liana.config.exception.MergeException;
import java.io.IOException;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AbstractJacksonComponentTest {

  @Mock
  private ObjectMapper mapper;
  private TestJacksonComponent component;

  @BeforeEach
  void setUp() {
    component = new TestJacksonComponent(mapper);
  }

  @Test
  @DisplayName("should throw NullPointerException when mapper is null")
  void shouldThrowNullPointerExceptionWhenMapperIsNull() {
    assertThrows(NullPointerException.class, () -> new TestJacksonComponent(null));
  }

  @Test
  @DisplayName("should return NullNode when value is null")
  void shouldReturnNullNodeWhenValueIsNull() {
    JsonNode node = component.textNode(null);
    assertSame(NullNode.getInstance(), node);
  }

  @Test
  @DisplayName("should return TextNode when value is not null")
  void shouldReturnTextNodeWhenValueIsNotNull() {
    JsonNode node = component.textNode("stage");
    assertInstanceOf(TextNode.class, node);
    assertEquals("stage", node.asText());
  }

  @Test
  @DisplayName("should execute supplier successfully and return result")
  void shouldExecuteSupplierSuccessfullyAndReturnResult() {
    ThrowingSupplier<String> supplier = () -> "result";

    String result = component.executeWithResult(supplier, "error message");

    assertEquals("result", result);
  }

  @Test
  @DisplayName("should throw ConversionException when supplier throws IOException")
  void shouldThrowConversionExceptionWhenSupplierThrowsIOException() {
    ThrowingSupplier<String> supplier = () -> {
      throw new IOException("I/O error occurred");
    };

    ConversionException exception = assertThrows(
        ConversionException.class,
        () -> component.executeWithResult(supplier, "I/O operation failed")
    );

    assertTrue(exception.getMessage().contains("I/O operation failed"));
    assertInstanceOf(IOException.class, exception.getCause());
  }

  @Test
  @DisplayName("should throw ConversionException when supplier throws IllegalArgumentException")
  void shouldThrowConversionExceptionWhenSupplierThrowsIllegalArgumentException() {
    ThrowingSupplier<String> supplier = () -> {
      throw new IllegalArgumentException("invalid");
    };

    ConversionException exception = assertThrows(
        ConversionException.class,
        () -> component.executeWithResult(supplier, "conversion failed")
    );

    assertEquals("conversion failed", exception.getMessage());
    assertInstanceOf(IllegalArgumentException.class, exception.getCause());
  }

  @Test
  @DisplayName("should throw ConversionException when supplier throws unexpected runtime exception")
  void shouldThrowConversionExceptionWhenSupplierThrowsUnexpectedRuntimeException() {
    ThrowingSupplier<String> supplier = () -> {
      throw new RuntimeException("error");
    };

    ConversionException exception = assertThrows(
        ConversionException.class,
        () -> component.executeWithResult(supplier, "unexpected error")
    );

    assertTrue(exception.getMessage().contains("unexpected runtime error during operation"));
    assertInstanceOf(RuntimeException.class, exception.getCause());
  }

  @Test
  @DisplayName("should throw ConversionException when supplier throws unexpected checked exception")
  void shouldThrowConversionExceptionWhenSupplierThrowsUnexpectedCheckedException() {
    ThrowingSupplier<String> supplier = () -> {
      throw new Exception("unknown exception");
    };

    ConversionException exception = assertThrows(
        ConversionException.class,
        () -> component.executeWithResult(supplier, "unexpected error")
    );

    assertTrue(exception.getMessage().contains("unexpected checked exception during operation"));
    assertInstanceOf(Exception.class, exception.getCause());
  }

  @Test
  @DisplayName("should execute action successfully without exception")
  void shouldExecuteActionSuccessfullyWithoutException() {
    assertDoesNotThrow(() -> component.executeAction(() -> {
    }, "should not fail"));
  }

  @Test
  @DisplayName("should throw MergeException when action throws IOException")
  void shouldThrowMergeExceptionWhenActionThrowsIOException() {
    ThrowingRunnable action = () -> {
      throw new IOException("I/O error");
    };

    MergeException exception = assertThrows(
        MergeException.class,
        () -> component.executeAction(action, "merge failed")
    );

    assertEquals("merge failed", exception.getMessage());
    assertInstanceOf(IOException.class, exception.getCause());
  }

  @Test
  @DisplayName("should throw MergeException when action throws unexpected exception")
  void shouldThrowMergeExceptionWhenActionThrowsUnexpectedException() {
    ThrowingRunnable action = () -> {
      throw new IllegalStateException("invalid state");
    };

    MergeException exception = assertThrows(
        MergeException.class,
        () -> component.executeAction(action, "unexpected error")
    );

    assertTrue(exception.getMessage().contains("unexpected error during"));
    assertInstanceOf(IllegalStateException.class, exception.getCause());
  }

  @Test
  @DisplayName("should expose static MAP_TYPE with correct type reference")
  void shouldExposeStaticMapTypeWithCorrectTypeReference() {
    TypeReference<Map<String, Object>> type = AbstractJacksonComponent.MAP_TYPE;
    assertNotNull(type);
  }

  private static class TestJacksonComponent extends AbstractJacksonComponent {

    protected TestJacksonComponent(ObjectMapper mapper) {
      super(mapper);
    }
  }
}
