package fr.jg.springrest.data.services;

import fr.jg.springrest.data.exceptions.*;
import fr.jg.springrest.data.pojo.PagedQuery;
import fr.jg.springrest.data.pojo.PagedResponse;
import fr.jg.springrest.data.pojo.PutResponse;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

public abstract class DataAccess<T, U, V, W> {

    private W mapper;

    public abstract PagedResponse<T> get(final PagedQuery<T> pagedQuery);

    public abstract Optional<T> patch(final UUID id, final T domainObject);

    public abstract PutResponse<T> put(final UUID id, final T domainObject);

    protected abstract Class<W> getMapperClass();

    private W getMapper() {
        if (this.mapper == null) {
            try {
                this.mapper = (W) Stream.of(this.getMapperClass().getFields())
                        .filter(field -> field.getType().equals(this.getMapperClass()))
                        .findAny()
                        .orElseThrow(() -> {
                            final NoMapperInstanceFoundException noMapperInstanceFoundException = new NoMapperInstanceFoundException(this.getMapperClass());
                            Logger.getLogger("DataAccess").log(Level.SEVERE,
                                    String.format("%s\nDetails: %s", noMapperInstanceFoundException.toString(), noMapperInstanceFoundException.getDetails())
                            );
                            return noMapperInstanceFoundException;
                        })
                        .get(null);
            } catch (final IllegalAccessException e) {
                final MapperIllegalAccessException mapperIllegalAccessException = new MapperIllegalAccessException(e, this.getMapperClass());
                Logger.getLogger("DataAccess").log(Level.SEVERE,
                        String.format("%s\nDetails: %s", mapperIllegalAccessException.toString(), mapperIllegalAccessException.getDetails())
                );
                throw mapperIllegalAccessException;
            }
        }

        return this.mapper;
    }

    protected Long getPage(final PagedQuery<T> pagedQuery) {
        final Long page = pagedQuery.getPage().orElse(0L);
        if (page < 0) {
            throw new PagingException("page", page);
        }

        return page;
    }

    protected Long getPerPage(final PagedQuery<T> pagedQuery) {
        final Long perPage = pagedQuery.getPerPage().orElse((long) Integer.MAX_VALUE);
        if (perPage < 0) {
            throw new PagingException("per_page", perPage);
        }

        return perPage;
    }

    protected T mapEntityToDomainObject(final U entity, final Class<U> entityClass, final Class<T> domainClass) {
        return (T) this.mapResources(Arrays.asList(entity), entityClass, domainClass).get(0);
    }

    protected U mapDomainObjectToEntity(final T domainObject, final Class<T> domainClass, final Class<U> entityClass) {
        return (U) this.mapResources(Arrays.asList(domainObject), domainClass, entityClass).get(0);
    }

    protected List mapResources(final List sourceObjects, final Class sourceClass, final Class targetClass) {
        final List resources = new ArrayList<>();
        final Method mapMethod = Stream.of(this.getMapperClass().getMethods())
                .filter(method -> method.getReturnType().equals(targetClass))
                .filter(method -> method.getParameterTypes() != null && method.getParameterCount() == 1 && method.getParameterTypes()[0].equals(sourceClass))
                .findAny()
                .orElseThrow(() -> {
                    final NoMapMethodFoundException noMapMethodFoundException = new NoMapMethodFoundException(this.getMapperClass(), sourceClass, targetClass);
                    Logger.getLogger("DataAccess").log(Level.SEVERE,
                            String.format("%s\nDetails: %s", noMapMethodFoundException.toString(), noMapMethodFoundException.getDetails())
                    );
                    return noMapMethodFoundException;
                });

        sourceObjects.forEach(resource -> {
            try {
                resources.add(mapMethod.invoke(this.getMapper(), resource));
            } catch (final IllegalAccessException e) {
                final MapperIllegalAccessException mapperIllegalAccessException = new MapperIllegalAccessException(e, this.getMapperClass(), sourceClass, targetClass, mapMethod);
                Logger.getLogger("DataAccess").log(Level.SEVERE,
                        String.format("%s\nDetails: %s", mapperIllegalAccessException.toString(), mapperIllegalAccessException.getDetails())
                );
                throw mapperIllegalAccessException;
            } catch (final InvocationTargetException e) {
                final MapInvocationTargetException mapInvocationTargetException = new MapInvocationTargetException(e, this.getMapperClass(), sourceClass, targetClass, mapMethod);
                Logger.getLogger("DataAccess").log(Level.SEVERE,
                        String.format("%s\nDetails: %s", mapInvocationTargetException.toString(), mapInvocationTargetException.getDetails())
                );
                throw mapInvocationTargetException;
            }
        });

        return resources;
    }
}
