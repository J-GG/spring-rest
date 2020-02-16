package fr.jg.springrest.data.services;

import fr.jg.springrest.data.exceptions.DetailedException;
import fr.jg.springrest.data.exceptions.InvalidParameterType;
import fr.jg.springrest.data.exceptions.PagingException;
import fr.jg.springrest.data.exceptions.ServerException;
import fr.jg.springrest.errors.SpringRestError;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Map;

@ControllerAdvice
public class GenericControllerExceptionHandler extends ResponseEntityExceptionHandler {
    @Value("${spring.profiles.active}")
    String profile;

    @ExceptionHandler( {ServerException.class})
    public final ResponseEntity<Map<String, Object>> handle500(final ServerException ex, final WebRequest webRequest) {
        final SpringRestError springRestError = new SpringRestError(HttpStatus.INTERNAL_SERVER_ERROR, webRequest, ex.getMessage(), ex.getClass().getSimpleName(), ex.getDetails());
        return new ResponseEntity<>(springRestError.toMapAttributes("dev".equals(this.profile)), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler( {PagingException.class, InvalidParameterType.class})
    public static ResponseEntity<Map<String, Object>> handle400(final DetailedException ex, final WebRequest webRequest) {
        final SpringRestError springRestError = new SpringRestError(HttpStatus.BAD_REQUEST, webRequest, ex.getMessage(), ex.getClass().getSimpleName(), ex.getDetails());
        return new ResponseEntity<>(springRestError.toMapAttributes(), HttpStatus.BAD_REQUEST);
    }
}
