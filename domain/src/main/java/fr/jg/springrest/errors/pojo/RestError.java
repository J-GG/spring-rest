package fr.jg.springrest.errors.pojo;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

public class RestError {

    private final Date timestamp;

    private final RestErrorRequest request;

    private final RestErrorResponse response;

    public RestError(final String method, final String path, final Map<String, String[]> queryString, final String httpCode, final Integer httpStatus, final String message,
                     final String exception, final Map<String, Object> details) {
        this.timestamp = new Date();
        this.request = new RestErrorRequest(method, path, queryString);
        this.response = new RestErrorResponse(httpCode, httpStatus, message, exception, details);
    }

    public Date getTimestamp() {
        return this.timestamp;
    }

    public RestErrorRequest getRequest() {
        return this.request;
    }

    public RestErrorResponse getResponse() {
        return this.response;
    }

    public Map<String, Object> toMapAttributes(final boolean includeDetails) {
        final Map<String, Object> map = new LinkedHashMap<>();
        map.put("timestamp", this.getTimestamp());
        map.put("request", this.request.toMapAttributes());
        map.put("response", this.response.toMapAttributes(includeDetails));

        return map;
    }
}
