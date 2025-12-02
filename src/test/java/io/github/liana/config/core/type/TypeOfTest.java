package io.github.liana.config.core.type;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class TypeOfTest {

  @Test
  @DisplayName("should capture simple type")
  void shouldCaptureSimpleType() {
    TypeOf<String> typeOf = new TypeOf<>() {
    };
    Type type = typeOf.getType();

    assertEquals(String.class, type);
  }

  @Test
  @DisplayName("should capture parameterized type")
  void shouldCaptureParameterizedType() {
    TypeOf<List<String>> typeOf = new TypeOf<>() {
    };
    Type type = typeOf.getType();

    assertInstanceOf(ParameterizedType.class, type);
    ParameterizedType paramType = (ParameterizedType) type;

    assertEquals(List.class, paramType.getRawType());
    assertEquals(String.class, paramType.getActualTypeArguments()[0]);
  }

  @Test
  @DisplayName("should throw exception when subclass is not parameterized")
  void shouldThrowExceptionWhenNotParameterized() {
    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
        () -> new TypeOf() {
        });

    assertEquals("TypeOf must be parameterized and instantiated as an anonymous class.",
        exception.getMessage());
  }

  @Test
  @DisplayName("should return the same type on multiple calls")
  void shouldReturnSameTypeOnMultipleCalls() {
    TypeOf<String> typeOf = new TypeOf<>() {
    };
    assertSame(typeOf.getType(), typeOf.getType());
  }
}