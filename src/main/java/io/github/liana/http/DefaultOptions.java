package io.github.liana.http;

import java.util.Map;

public class DefaultOptions implements Options {
    private final Map<String, String> headers;
    private final Map<String, String> params;

    public DefaultOptions(Map<String, String> headers, Map<String, String> params) {
        this.headers = headers;
        this.params = params;
    }

    @Override
    public Map<String, String> headers() {
        return this.headers;
    }

    @Override
    public Map<String, String> params() {
        return this.params;
    }
}
