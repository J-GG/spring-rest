package fr.jg.springrest.exceptions;

public class MapperIllegalAccessException extends RuntimeException {
    public MapperIllegalAccessException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
