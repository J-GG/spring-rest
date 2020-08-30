package fr.jg.springrest.dto;

import java.util.HashMap;
import java.util.Map;

public class WrappedPagedResource<T> {

    private Map<String, Object> aggregates;

    private T resource;

    public WrappedPagedResource() {
        this.aggregates = new HashMap<>();
    }

    public WrappedPagedResource(final T resource, final Map<String, Object> aggregates) {
        this.resource = resource;
        this.aggregates = aggregates;
    }

    public Map<String, Object> getAggregates() {
        return this.aggregates;
    }

    public void setAggregates(final Map<String, Object> aggregates) {
        this.aggregates = aggregates;
    }

    public T getResource() {
        return this.resource;
    }

    public void setResource(final T resource) {
        this.resource = resource;
    }
}
