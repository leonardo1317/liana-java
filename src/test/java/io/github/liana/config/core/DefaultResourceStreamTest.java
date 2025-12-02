package io.github.liana.config.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.io.IOException;
import java.io.InputStream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class DefaultResourceStreamTest {

  @Test
  @SuppressWarnings("resource")
  @DisplayName("should throw NullPointerException if resourceName is null")
  void shouldThrowIfResourceNameIsNull() {
    InputStream mockStream = mock(InputStream.class);
    NullPointerException exception = assertThrows(NullPointerException.class,
        () -> new DefaultResourceStream(null, mockStream));
    assertEquals("name must not be null", exception.getMessage());
  }

  @Test
  @SuppressWarnings("resource")
  @DisplayName("should throw NullPointerException if inputStream is null")
  void shouldThrowIfInputStreamIsNull() {
    NullPointerException exception = assertThrows(NullPointerException.class,
        () -> new DefaultResourceStream("config.yaml", null));
    assertEquals("stream must not be null", exception.getMessage());
  }

  @Test
  @DisplayName("should store resourceName and inputStream correctly")
  void shouldStoreFieldsCorrectly() {
    InputStream mockStream = mock(InputStream.class);
    DefaultResourceStream resource = new DefaultResourceStream("config.yaml", mockStream);

    assertEquals("config.yaml", resource.name());
    assertSame(mockStream, resource.stream());
  }

  @Test
  @DisplayName("should close the inputStream when close() is called")
  void shouldCloseInputStream() throws Exception {
    InputStream mockStream = mock(InputStream.class);
    DefaultResourceStream resource = new DefaultResourceStream("config.yaml", mockStream);

    resource.close();

    verify(mockStream).close();
  }

  @Test
  @DisplayName("should propagate IOException from inputStream.close()")
  void shouldPropagateIOExceptionOnClose() throws Exception {
    InputStream mockStream = mock(InputStream.class);
    doThrow(new IOException("fail")).when(mockStream).close();

    DefaultResourceStream resource = new DefaultResourceStream("config.yaml", mockStream);

    IOException exception = assertThrows(IOException.class, resource::close);
    assertEquals("fail", exception.getMessage());
  }
}
