package fr.jg.springrest.errors.pojo;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Error block containing the information about the response.
 * <p>
 * The purpose of the response block is to enlighten the user as to why the error occurred and how to fix it.
 */
public class RestErrorResponse {

    /**
     * HTTP code of the response (ex. 404)
     */
    private final Integer httpCode;

    /**
     * HTTP status of the response (ex. NOT_FOUND)
     */
    private final String httpStatus;

    /**
     * Name of the exception class causing the error
     */
    private final String exception;

    /**
     * Message describing the exception
     */
    private final String message;

    /**
     * Details about the exception
     * <p>
     * Defined by the exception itself, the keys are not consistent from one exception to another.
     */
    private final Map<String, Object> details;

    /**
     * Constructor.
     *
     * @param httpStatus the HTTP status
     * @param httpCode   the HTTP code
     * @param message    the message
     * @param exception  the exception class name
     * @param details    the details
     */
    public RestErrorResponse(final String httpStatus, final Integer httpCode, final String message, final String exception, final Map<String, Object> details) {
        this.httpStatus = httpStatus;
        this.httpCode = httpCode;
        this.message = message;
        this.exception = exception;
        this.details = details;
    }


    /**
     * Gets the optional HTTP code of the response (ex. 404).
     *
     * @return an optional HTTP code
     */
    public Optional<Integer> getHttpCode() {
        return Optional.ofNullable(this.httpCode);
    }

    /**
     * Gets the optional HTTP status of the response (ex. NOT_FOUND)
     *
     * @return the optional HTTP status
     */
    public Optional<String> getHttpStatus() {
        return Optional.ofNullable(this.httpStatus);
    }

    /**
     * Gets the optional name of the exception class causing the error.
     *
     * @return the optional name
     */
    public Optional<String> getExeption() {
        return Optional.ofNullable(this.exception);
    }

    /**
     * Gets the optional message describing the exception.
     *
     * @return the optional message
     */
    public Optional<String> getMessage() {
        return Optional.ofNullable(this.message);
    }

    /**
     * Gets the optional details about the exception.
     *
     * @return the optional details
     */
    public Optional<Map<String, Object>> getDetails() {
        return Optional.ofNullable(this.details);
    }

    /**
     * Gets the response error block information as a map.
     *
     * @param includeDetails whether more information should be added to the map or not. Useful to prevent the user from accessing sensitive data.
     * @return a map containing all of the information about the response error block
     */
    public Map<String, Object> toMapAttributes(final boolean includeDetails) {
        final Map<String, Object> map = new LinkedHashMap<>();
        map.put("http_code", this.getHttpCode().isPresent() ? this.httpCode : "No HTTP error code available");
        map.put("http_status", this.getHttpStatus().orElse("No HTTP status available"));
        if (includeDetails) {
            map.put("exception", this.getExeption().orElse("No exception available"));
            map.put("message", this.getMessage().orElse("No message available"));
            map.put("details", this.getDetails().orElse(new HashMap<>()));
        }

        return map;
    }
}
