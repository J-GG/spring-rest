package fr.jg.springrest.data.exceptions;

public class IllegalAccessPruningException extends RuntimeException {
    public IllegalAccessPruningException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
