package fr.jg.springrest.exceptions;

public class NoMapMethodFoundException extends RuntimeException {
    public NoMapMethodFoundException(final String message) {
        super(message);
    }
}
