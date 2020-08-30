package fr.jg.springrest.data.filters;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.catalina.connector.RequestFacade;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * A Filter intercepting a request containing the `per_page` query string parameter to modify its name.
 */
@Component
public class PatchFilter implements Filter {
    @Override
    public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain filterChain) throws IOException, ServletException {
        final String perPageParam = request.getParameter("per_page");

        if ("PATCH".equals(((RequestFacade) request).getMethod())) {
            final BodyRequestWrapper bodyRequestWrapper = new BodyRequestWrapper((HttpServletRequest) request);
            final ObjectMapper mapper = new ObjectMapper();
            final JsonNode body = mapper.readTree(bodyRequestWrapper.getBody());
            bodyRequestWrapper.setAttribute("PATCH", body);
            filterChain.doFilter(bodyRequestWrapper, response);
        } else {
            filterChain.doFilter(request, response);
        }
    }
}

