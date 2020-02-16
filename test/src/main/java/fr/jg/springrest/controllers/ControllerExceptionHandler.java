package fr.jg.springrest.controllers;

import fr.jg.springrest.errors.SpringRestError;
import fr.jg.springrest.exceptions.InvalidResourceException;
import fr.jg.springrest.exceptions.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Map;

@ControllerAdvice
public class ControllerExceptionHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler( {ResourceNotFoundException.class})
    public static ResponseEntity<Map<String, Object>> handleResourceNotFoundException(final ResourceNotFoundException ex, final WebRequest webRequest) {
        final SpringRestError springRestError = new SpringRestError(HttpStatus.NOT_FOUND, webRequest, ex.getMessage(), ex.getClass().getSimpleName(), ex.getDetails());
        return new ResponseEntity<>(springRestError.toMapAttributes(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler( {InvalidResourceException.class})
    public static ResponseEntity<Map<String, Object>> handleInvalidResourceException(final InvalidResourceException ex, final WebRequest webRequest) {
        final SpringRestError springRestError = new SpringRestError(HttpStatus.BAD_REQUEST, webRequest, ex.getMessage(), ex.getClass().getSimpleName(), ex.getDetails());
        return new ResponseEntity<>(springRestError.toMapAttributes(), HttpStatus.BAD_REQUEST);
    }
}
