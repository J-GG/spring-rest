package com.example.toolbox;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PagedResponse<T> extends PagedResource<T> {

    private Integer size;

    private Integer totalPages;

    private Long totalResources;

    private List<T> resources;

    public PagedResponse(final PagedResource<T> pagedResource, final Integer size, final Integer totalPages, final Long totalResources, final List<T> resources) {
        super(pagedResource.getFields(), pagedResource.getPage().orElse(null), pagedResource.getPerPage().orElse(null), pagedResource.getSort(), pagedResource.getFilters());
        this.size = size;
        this.totalPages = totalPages;
        this.totalResources = totalResources;
        this.resources = resources;
    }

    public Map<String, String> getHeaders() {
        final Map<String, String> headers = new HashMap<>();
        headers.put("page", getPage().toString());
        headers.put("per_page", getPerPage().toString());
        headers.put("size", size.toString());
        headers.put("total_pages", totalPages.toString());
        headers.put("total_resources", totalResources.toString());

        return headers;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(final Integer size) {
        this.size = size;
    }

    public Integer getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(final Integer totalPages) {
        this.totalPages = totalPages;
    }

    public Long getTotalResources() {
        return totalResources;
    }

    public void setTotalResources(final Long totalResources) {
        this.totalResources = totalResources;
    }

    public List<T> getResources() {
        return resources;
    }

    public void setResources(final List<T> resources) {
        this.resources = resources;
    }
}
