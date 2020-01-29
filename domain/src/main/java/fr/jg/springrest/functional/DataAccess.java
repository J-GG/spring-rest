package fr.jg.springrest.functional;

import fr.jg.springrest.PagedResource;
import fr.jg.springrest.PagedResponse;

public abstract class DataAccess<T, U, V, W> {
    public abstract PagedResponse<T> get(PagedResource<T> pagedResource);
}
