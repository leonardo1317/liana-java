package io.github.liana.clients.httpclient;

import java.util.Map;

public interface Options {
    Map<String, String> headers();

    Map<String, String> params();
}
