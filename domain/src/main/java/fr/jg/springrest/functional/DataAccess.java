package fr.jg.springrest.functional;

import fr.jg.springrest.PagedResource;
import fr.jg.springrest.PagedResponse;

public interface DataAccess<T, U, V, W> {
    PagedResponse<T> get(PagedResource<T> pagedResource, V repository, final FilterableFieldFilter filterableFieldFilter, SortableFieldFilter sortableFieldFilter);
}
