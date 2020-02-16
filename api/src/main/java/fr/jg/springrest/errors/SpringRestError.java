package fr.jg.springrest.errors;

import fr.jg.springrest.errors.pojo.RestError;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.http.HttpStatus;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import java.util.Map;
import java.util.Optional;

public class SpringRestError {

    private final RestError restError;

    public SpringRestError(final Map<String, Object> defaultErrorAttributesMap, final WebRequest webRequest) {
        final String path = defaultErrorAttributesMap.containsKey("path") ? defaultErrorAttributesMap.get("path").toString() : null;
        final String method = ((ServletWebRequest) webRequest).getRequest().getMethod();
        final Map<String, String[]> queryString = ((ServletWebRequest) webRequest).getRequest().getParameterMap();
        final Integer httpStatus = defaultErrorAttributesMap.containsKey("status") ? Integer.valueOf(defaultErrorAttributesMap.get("status").toString()) : null;
        final String httpCode = defaultErrorAttributesMap.containsKey("error") ? defaultErrorAttributesMap.get("error").toString() : null;
        final String message = defaultErrorAttributesMap.containsKey("message") ? defaultErrorAttributesMap.get("message").toString() : null;
        final String exception = Optional.ofNullable(webRequest.getAttribute(DefaultErrorAttributes.class.getName() + ".ERROR", RequestAttributes.SCOPE_REQUEST))
                .map(e -> e.getClass().getSimpleName()).orElse(null);

        this.restError = new RestError(method, path, queryString, httpCode, httpStatus, message, exception, null);
    }

    public SpringRestError(final HttpStatus httpStatusCode, final WebRequest webRequest, final String message, final String exception, final Map<String, Object> details) {
        final String path = ((ServletWebRequest) webRequest).getRequest().getRequestURI();
        final Map<String, String[]> queryString = ((ServletWebRequest) webRequest).getRequest().getParameterMap();
        final String method = ((ServletWebRequest) webRequest).getRequest().getMethod();
        final Integer httpStatus = httpStatusCode.value();
        final String httpCode = httpStatusCode.getReasonPhrase();

        this.restError = new RestError(method, path, queryString, httpCode, httpStatus, message, exception, details);
    }

    public Map<String, Object> toMapAttributes() {
        return this.toMapAttributes(true);
    }

    public Map<String, Object> toMapAttributes(final boolean includeDetails) {
        return this.restError.toMapAttributes(includeDetails);
    }

}
