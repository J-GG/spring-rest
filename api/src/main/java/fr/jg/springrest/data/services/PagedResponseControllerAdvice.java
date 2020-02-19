package fr.jg.springrest.data.services;

import fr.jg.springrest.data.pojo.PagedResponse;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.util.Collections;

/**
 * A ControllerAdvice intercepting the response of controllers returning {@link PagedResponse} in order to add custom headers.
 */
@ControllerAdvice
public class PagedResponseControllerAdvice implements ResponseBodyAdvice<Object> {

    @Override
    public boolean supports(final MethodParameter methodParameter, final Class<? extends HttpMessageConverter<?>> aClass) {
        return methodParameter.getNestedParameterType().equals(PagedResponse.class);
    }

    @Override
    public Object beforeBodyWrite(final Object body, final MethodParameter returnType, final MediaType selectedContentType,
                                  final Class<? extends HttpMessageConverter<?>> selectedConverterType, final ServerHttpRequest request, final ServerHttpResponse response) {
        final PagedResponse<?> pagedResponse = (PagedResponse<?>) body;
        response.getHeaders().put("page", Collections.singletonList(pagedResponse.getPage().orElse(0L).toString()));
        response.getHeaders().put("per_page", Collections.singletonList(pagedResponse.getPerPage().orElse(0L).toString()));
        response.getHeaders().put("size", Collections.singletonList(pagedResponse.getSize().orElse(0L).toString()));
        response.getHeaders().put("total_pages", Collections.singletonList(pagedResponse.getTotalPages().orElse(0L).toString()));
        response.getHeaders().put("total_resources", Collections.singletonList(pagedResponse.getTotalResources().orElse(0L).toString()));

        return pagedResponse.getResources();
    }
}
