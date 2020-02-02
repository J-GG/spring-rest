package fr.jg.springrest.errors.pojo;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public class RestErrorRequest {

    private final String path;

    private final Map<String, String[]> queryString;

    private final String method;

    public RestErrorRequest(final String method, final String path, final Map<String, String[]> queryString) {
        this.method = method;
        this.path = path;
        this.queryString = queryString;
    }

    public Optional<String> getMethod() {
        return Optional.ofNullable(this.method);
    }

    public Optional<String> getPath() {
        return Optional.ofNullable(this.path);
    }

    public Optional<Map<String, String[]>> getQueryString() {
        return Optional.ofNullable(this.queryString);
    }

    public Map<String, Object> toMapAttributes() {
        final Map<String, Object> map = new LinkedHashMap<>();
        map.put("method", this.getMethod().orElse("No HTTP method available"));
        map.put("path", this.getPath().orElse("No path available"));
        map.put("query_string", this.getQueryString().orElse(new HashMap<>()));

        return map;
    }
}
