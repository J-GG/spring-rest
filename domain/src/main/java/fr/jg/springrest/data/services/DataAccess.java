package fr.jg.springrest.data.services;

import fr.jg.springrest.data.exceptions.*;
import fr.jg.springrest.data.pojo.PagedResource;
import fr.jg.springrest.data.pojo.PagedResponse;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
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
                    .orElseThrow(() -> {
                        NoMapperInstanceFoundException noMapperInstanceFoundException = new NoMapperInstanceFoundException(mapperClass);
                        Logger.getLogger("DataAccess").log(Level.SEVERE,
                                String.format("%s\nDetails: %s", noMapperInstanceFoundException.toString(), noMapperInstanceFoundException.getDetails())
                        );
                        return noMapperInstanceFoundException;
                    })
                    .get(null);

            final Method mapMethod = Stream.of(mapperClass.getMethods())
                    .filter(method -> method.getReturnType().equals(domainClass))
                    .filter(method -> method.getParameterTypes() != null && method.getParameterCount() == 1 && method.getParameterTypes()[0].equals(entityClass))
                    .findAny()
                    .orElseThrow(() -> {
                        final NoMapMethodFoundException noMapMethodFoundException = new NoMapMethodFoundException(mapperClass, domainClass, entityClass);
                        Logger.getLogger("DataAccess").log(Level.SEVERE,
                                String.format("%s\nDetails: %s", noMapMethodFoundException.toString(), noMapMethodFoundException.getDetails())
                        );
                        return noMapMethodFoundException;
                    });

            resourceEntities.forEach(resource -> {
                try {
                    resources.add((T) mapMethod.invoke(mapper, resource));
                } catch (final IllegalAccessException e) {
                    final MapperIllegalAccessException mapperIllegalAccessException = new MapperIllegalAccessException(e, mapperClass, entityClass, domainClass, mapMethod);
                    Logger.getLogger("DataAccess").log(Level.SEVERE,
                            String.format("%s\nDetails: %s", mapperIllegalAccessException.toString(), mapperIllegalAccessException.getDetails())
                    );
                    throw mapperIllegalAccessException;
                } catch (final InvocationTargetException e) {
                    final MapInvocationTargetException mapInvocationTargetException = new MapInvocationTargetException(e, mapperClass, entityClass, domainClass, mapMethod);
                    Logger.getLogger("DataAccess").log(Level.SEVERE,
                            String.format("%s\nDetails: %s", mapInvocationTargetException.toString(), mapInvocationTargetException.getDetails())
                    );
                    throw mapInvocationTargetException;
                }
            });
        } catch (final IllegalAccessException e) {
            final MapperIllegalAccessException mapperIllegalAccessException = new MapperIllegalAccessException(e, mapperClass, entityClass, domainClass);
            Logger.getLogger("DataAccess").log(Level.SEVERE,
                    String.format("%s\nDetails: %s", mapperIllegalAccessException.toString(), mapperIllegalAccessException.getDetails())
            );
            throw mapperIllegalAccessException;
        }

        return resources;
    }
}
