package io.github.liana.http;

import java.util.Map;

public interface Options {
    Map<String, String> headers();

    Map<String, String> params();
}
