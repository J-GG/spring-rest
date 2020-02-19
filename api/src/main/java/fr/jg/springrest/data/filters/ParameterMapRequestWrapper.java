package fr.jg.springrest.data.filters;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Map;
import java.util.TreeMap;

/**
 * Class allowing to adapt the request parameters of a query.
 */
public class ParameterMapRequestWrapper extends HttpServletRequestWrapper {

    private final Map<String, String[]> modifiableParameters;

    private Map<String, String[]> allParameters;

    public ParameterMapRequestWrapper(final HttpServletRequest request, final Map<String, String[]> additionalParams) {
        super(request);
        this.modifiableParameters = new TreeMap<>();
        this.modifiableParameters.putAll(additionalParams);
    }

    @Override
    public String getParameter(final String name) {
        final String[] strings = this.getParameterMap().get(name);
        if (strings != null) {
            return strings[0];
        }
        return super.getParameter(name);
    }

    @Override
    public Map<String, String[]> getParameterMap() {
        if (this.allParameters == null) {
            this.allParameters = new TreeMap<>();
            this.allParameters.putAll(super.getParameterMap());
            this.allParameters.putAll(this.modifiableParameters);
        }

        return Collections.unmodifiableMap(this.allParameters);
    }

    @Override
    public Enumeration<String> getParameterNames() {
        return Collections.enumeration(this.getParameterMap().keySet());
    }

    @Override
    public String[] getParameterValues(final String name) {
        return this.getParameterMap().get(name);
    }
}