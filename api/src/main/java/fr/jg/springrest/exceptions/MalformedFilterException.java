package fr.jg.springrest.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class MalformedFilterException extends RuntimeException {
    public MalformedFilterException(final String message) {
        super(message);
    }
}
