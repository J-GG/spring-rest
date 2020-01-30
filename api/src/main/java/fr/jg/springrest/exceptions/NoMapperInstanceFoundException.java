package fr.jg.springrest.exceptions;

public class NoMapperInstanceFoundException extends RuntimeException {
    public NoMapperInstanceFoundException(final String message) {
        super(message);
    }
}
