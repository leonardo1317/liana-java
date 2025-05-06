package io.github.liana.http;

import java.util.HashMap;
import java.util.Map;

public class RequestOptions {
    private final Map<String, String> headers;
    private final Map<String, String> params;

    private RequestOptions(Builder builder) {
        this.headers = builder.headers;
        this.params = builder.params;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public Map<String, String> getParams() {
        return params;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final Map<String, String> headers;
        private final Map<String, String> params;

        private Builder() {
            headers = new HashMap<>();
            params = new HashMap<>();
        }


        public Builder withHeaders(Map<String, String> headers) {
            this.headers.putAll(headers);
            return this;
        }

        public Builder withHeader(String key, String value) {
            this.headers.put(key, value);
            return this;
        }

        public Builder withParams(Map<String, String> params) {
            this.params.putAll(params);
            return this;
        }

        public Builder withParam(String key, String value) {
            this.params.put(key, value);
            return this;
        }

        public Options build() {
            return new DefaultOptions(
                    new HashMap<>(this.headers),
                    new HashMap<>(this.params)
            );
        }
    }
}
