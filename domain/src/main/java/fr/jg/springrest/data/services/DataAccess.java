package fr.jg.springrest.data.services;

import fr.jg.springrest.data.exceptions.*;
import fr.jg.springrest.data.pojo.PagedResource;
import fr.jg.springrest.data.pojo.PagedResponse;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public interface DataAccess<T, U, V, W> {
    PagedResponse<T> get(PagedResource<T> pagedResource, V repository, final FilterableFieldFilter filterableFieldFilter, SortableFieldFilter sortableFieldFilter);

    default Long getPage(final PagedResource<T> pagedResource) {
        final Long page = pagedResource.getPage().orElse(0L);
        if (page < 0) {
            throw new PagingException("page", page);
        }

        return page;
    }

    default Long getPerPage(final PagedResource<T> pagedResource) {
        final Long perPage = pagedResource.getPer_page().orElse((long) Integer.MAX_VALUE);
        if (perPage < 0) {
            throw new PagingException("per_page", perPage);
        }

        return perPage;
    }

    default List<T> mapResource(final List<U> resourceEntities, final Class<T> domainClass, final Class<U> entityClass, final Class<W> mapperClass) {
        final List<T> resources = new ArrayList<>();
        try {
            final W mapper = (W) Stream.of(mapperClass.getFields())
                    .filter(field -> field.getType().equals(mapperClass))
                    .findAny()
                    .orElseThrow(() -> new NoMapperInstanceFoundException(String.format("No instance of the mapper %s could be found.", mapperClass)))
                    .get(null);

            final Method mapMethod = Stream.of(mapperClass.getMethods())
                    .filter(method -> method.getReturnType().equals(domainClass))
                    .filter(method -> method.getParameterTypes() != null && method.getParameterCount() == 1 && method.getParameterTypes()[0].equals(entityClass))
                    .findAny()
                    .orElseThrow(() -> new NoMapMethodFoundException(String.format("No method could be found to map from object %s to entity %s.", domainClass, entityClass)));
            resourceEntities.forEach(resource -> {
                try {
                    resources.add((T) mapMethod.invoke(mapper, resource));
                } catch (final IllegalAccessException e) {
                    throw new MapperIllegalAccessException(
                            String.format("Forbidden access to the %s map method in method while trying to map %s to %s.", mapperClass, entityClass, domainClass),
                            e);
                } catch (final InvocationTargetException e) {
                    throw new MapInvocationTargetException(
                            String.format("An exception occurred in the %s map method while mapping an object %s to %s.", mapperClass, entityClass, domainClass),
                            e);
                }
            });
        } catch (final IllegalAccessException e) {
            throw new MapperIllegalAccessException(String.format("Forbidden access to the %s mapper.", mapperClass), e);
        }

        return resources;
    }
}
