package fr.jg.springrest.controllers;

import fr.jg.springrest.data.exceptions.PagingException;
import fr.jg.springrest.data.exceptions.ServerException;
import fr.jg.springrest.errors.SpringRestError;
import fr.jg.springrest.exceptions.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Map;

@ControllerAdvice
public class ControllerExceptionHandler extends ResponseEntityExceptionHandler {

    @Value("${spring.profiles.active}")
    String profile;

    @ExceptionHandler( {PagingException.class})
    public final ResponseEntity<Map<String, Object>> handlePagingException(final PagingException ex, final WebRequest webRequest) {
        final SpringRestError springRestError = new SpringRestError(HttpStatus.BAD_REQUEST, webRequest, ex.getMessage(), ex.getClass().getSimpleName(), ex.getDetails());
        return new ResponseEntity<>(springRestError.toMapAttributes(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler( {ResourceNotFoundException.class})
    public final ResponseEntity<Map<String, Object>> handleResourceNotFoundException(final ResourceNotFoundException ex, final WebRequest webRequest) {
        final SpringRestError springRestError = new SpringRestError(HttpStatus.NOT_FOUND, webRequest, ex.getMessage(), ex.getClass().getSimpleName(), ex.getDetails());
        return new ResponseEntity<>(springRestError.toMapAttributes(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler( {ServerException.class})
    public final ResponseEntity<Map<String, Object>> handleServerException(final ServerException ex, final WebRequest webRequest) {
        final SpringRestError springRestError = new SpringRestError(HttpStatus.INTERNAL_SERVER_ERROR, webRequest, ex.getMessage(), ex.getClass().getSimpleName(), ex.getDetails());
        return new ResponseEntity<>(springRestError.toMapAttributes("dev".equals(this.profile)), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
