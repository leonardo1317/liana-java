package io.github.liana.config.exception;

public class MissingConfigException extends RuntimeException {
    public MissingConfigException(String message) {
        super(message);
    }
}
