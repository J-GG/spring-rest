package fr.jg.springrest.data.pojo;

import fr.jg.springrest.data.enumerations.SortingOrderEnum;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Contains the information of a REST response in order to page, sort and filter the resource.
 *
 * @param <T> The resource sent back to the client.
 */
public class PagedResponse<T> extends PagedQuery<T> {

    /**
     * The number of resources on the page.
     */
    private Long size;

    /**
     * The total number of pages.
     */
    private Long totalPages;

    /**
     * The total number of resources.
     */
    private Long totalResources;

    /**
     * The list of resources on this page.
     */
    private List<T> resources;

    /**
     * Constructor.
     *
     * @param sort           The map of fields based on which the resources should be sorted out.
     * @param filters        The list of filters to filter the list of resources.
     * @param page           The page number.
     * @param perPage        The number of resources per page.
     * @param size           The number of resources on the page.
     * @param totalPages     The total number of pages.
     * @param totalResources The total number of resources.
     * @param resources      The list of resources on this page.
     */
    public PagedResponse(final Map<String, SortingOrderEnum> sort, final List<FilterCriterion> filters, final Long page, final Long perPage,
                         final Long size, final Long totalPages, final Long totalResources, final List<T> resources) {
        super(page, perPage, sort, filters);
        this.size = size;
        this.totalPages = totalPages;
        this.totalResources = totalResources;
        this.resources = resources;
    }

    /**
     * Gets the number of resources on the page.
     *
     * @return The number of resources on the page.
     */
    public Optional<Long> getSize() {
        return Optional.ofNullable(this.size);
    }

    /**
     * Sets the number of resources on the page.
     *
     * @param size The number of resources on the page.
     */
    public void setSize(final Long size) {
        this.size = size;
    }

    /**
     * Gets the total number of pages.
     *
     * @return The total number of pages.
     */
    public Optional<Long> getTotalPages() {
        return Optional.ofNullable(this.totalPages);
    }

    /**
     * Sets the total number of pages.
     *
     * @param totalPages The total number of pages.
     */
    public void setTotalPages(final Long totalPages) {
        this.totalPages = totalPages;
    }

    /**
     * Gets the total number of resources.
     *
     * @return The total number of resources.
     */
    public Optional<Long> getTotalResources() {
        return Optional.ofNullable(this.totalResources);
    }

    /**
     * Sets the total number of resources.
     *
     * @param totalResources The total number of resources.
     */
    public void setTotalResources(final Long totalResources) {
        this.totalResources = totalResources;
    }

    /**
     * Gets the list of resources on this page.
     *
     * @return The list of resources on this page.
     */
    public List<T> getResources() {
        return this.resources;
    }

    /**
     * Sets the list of resources on this page.
     *
     * @param resources The list of resources on this page.
     */
    public void setResources(final List<T> resources) {
        this.resources = resources;
    }
}
