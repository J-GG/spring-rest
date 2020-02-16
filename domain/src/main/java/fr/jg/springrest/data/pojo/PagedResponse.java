package fr.jg.springrest.data.pojo;

import fr.jg.springrest.data.enumerations.SortingOrderEnum;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class PagedResponse<T> extends PagedQuery<T> {

    private Long size;

    private Long totalPages;

    private Long totalResources;

    private List<T> resources;

    public PagedResponse(final Map<String, SortingOrderEnum> sort, final List<FilterCriteria> filters, final Long page, final Long perPage,
                         final Long size, final Long totalPages, final Long totalResources, final List<T> resources) {
        super(page, perPage, sort, filters);
        this.size = size;
        this.totalPages = totalPages;
        this.totalResources = totalResources;
        this.resources = resources;
    }

    public Optional<Long> getSize() {
        return Optional.ofNullable(this.size);
    }

    public void setSize(final Long size) {
        this.size = size;
    }

    public Optional<Long> getTotalPages() {
        return Optional.ofNullable(this.totalPages);
    }

    public void setTotalPages(final Long totalPages) {
        this.totalPages = totalPages;
    }

    public Optional<Long> getTotalResources() {
        return Optional.ofNullable(this.totalResources);
    }

    public void setTotalResources(final Long totalResources) {
        this.totalResources = totalResources;
    }

    public List<T> getResources() {
        return this.resources;
    }

    public void setResources(final List<T> resources) {
        this.resources = resources;
    }
}
