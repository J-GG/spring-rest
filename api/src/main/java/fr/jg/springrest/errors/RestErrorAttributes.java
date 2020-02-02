package fr.jg.springrest.errors;

import fr.jg.springrest.data.exceptions.ServerException;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.web.context.request.WebRequest;

import java.util.Map;

/**
 * Provides the default response to the client after an error occurred.
 * <p>
 * Makes use of the {@link SpringRestError} to define the attributes which should be sent back.
 * To make it the default Spring error template, use the {@link org.springframework.context.annotation.Bean} annotation in a configuration class.
 */
public class RestErrorAttributes extends DefaultErrorAttributes {

    private final boolean includeServerExceptionsDetails;

    public RestErrorAttributes(final boolean includeServerExceptionsDetails) {
        this.includeServerExceptionsDetails = includeServerExceptionsDetails;
    }

    @Override
    public Map<String, Object> getErrorAttributes(final WebRequest webRequest, final boolean includeStackTrace) {
        final Map<String, Object> defaultErrorAttributesMap = super.getErrorAttributes(webRequest, includeStackTrace);
        final SpringRestError restError = new SpringRestError(defaultErrorAttributesMap, webRequest);
        final boolean includeDetails = this.getError(webRequest) instanceof ServerException ? this.includeServerExceptionsDetails : true;
        return restError.toMapAttributes(includeDetails);
    }
}
