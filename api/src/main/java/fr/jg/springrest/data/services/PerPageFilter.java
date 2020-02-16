package fr.jg.springrest.data.services;

import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

/**
 * A Filter intercepting a request containing the `per_page` query string parameter to modify its name.
 */
@Component
public class PerPageFilter implements Filter {
    @Override
    public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain filterChain) throws IOException, ServletException {
        final String perPageParam = request.getParameter("per_page");

        if (perPageParam != null) {
            final Map<String, String[]> extraParams = new TreeMap<>();
            extraParams.put("perPage", new String[] {perPageParam});
            final HttpServletRequest wrappedRequest = new ParameterMapRequestWrapper((HttpServletRequest) request, extraParams);
            filterChain.doFilter(wrappedRequest, response);
        } else {
            filterChain.doFilter(request, response);
        }
    }
}

