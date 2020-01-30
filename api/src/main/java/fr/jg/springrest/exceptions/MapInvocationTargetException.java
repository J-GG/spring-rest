package fr.jg.springrest.exceptions;

public class MapInvocationTargetException extends RuntimeException {
    public MapInvocationTargetException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
