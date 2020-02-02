package fr.jg.springrest.data.exceptions;

public class NoMapMethodFoundException extends RuntimeException {
    public NoMapMethodFoundException(final String message) {
        super(message);
    }
}
