package fr.jg.springrest.errors.pojo;

import java.util.LinkedHashMap;
import java.util.Map;

public class DetailedException extends RuntimeException {

    protected final Map<String, Object> details = new LinkedHashMap<>();

    public DetailedException(final String message) {
        super(message);
    }

    public DetailedException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public Map<String, Object> getDetails() {
        return this.details;
    }
}
