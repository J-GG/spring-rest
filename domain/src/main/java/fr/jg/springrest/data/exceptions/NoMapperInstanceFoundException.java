package fr.jg.springrest.data.exceptions;

public class NoMapperInstanceFoundException extends RuntimeException {
    public NoMapperInstanceFoundException(final String message) {
        super(message);
    }
}
