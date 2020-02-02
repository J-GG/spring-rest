package fr.jg.springrest.data.exceptions;

import java.util.LinkedHashMap;
import java.util.Map;

public class RestException extends RuntimeException {

    protected final Map<String, Object> details = new LinkedHashMap<>();

    RestException(final String message) {
        super(message);
    }

    RestException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public Map<String, Object> getDetails() {
        return this.details;
    }
}
