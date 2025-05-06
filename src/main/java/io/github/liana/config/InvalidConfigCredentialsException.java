package io.github.liana.config;

public class InvalidConfigCredentialsException extends IllegalArgumentException {
    public InvalidConfigCredentialsException(String message) {
        super(message);
    }
}
