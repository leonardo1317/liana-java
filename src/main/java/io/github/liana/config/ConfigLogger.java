package io.github.liana.config;

import java.util.function.Supplier;

public interface ConfigLogger {
    void info(Supplier<String> message);

    void debug(Supplier<String> message);

    void warn(Supplier<String> message);

    void error(Supplier<String> message, Exception e);
}
