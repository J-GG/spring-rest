package fr.jg.springrest.data.services;

import fr.jg.springrest.data.exceptions.*;
import fr.jg.springrest.data.pojo.PagedQuery;
import fr.jg.springrest.data.pojo.PagedResponse;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

/**
 * A class enabling to manipulate more easily the simplest operation on resources.
 *
 * @param <T> The domain object.
 * @param <U> The entity.
 * @param <V> The repository.
 * @param <W> The mapper.
 */
public abstract class DataAccess<T, U, V, W> {

    /**
     * The class of the domain object.
     */
    protected final Class<T> domainClass;

    /**
     * The class of the entity.
     */
    protected final Class<U> entityClass;

    /**
     * The class of the mapper.
     */
    protected final Class<W> mapperClass;

    /**
     * The mapper converting a domain object to an entity and vice-versa.
     */
    private W mapper;

    /**
     * Constructor.
     */
    public DataAccess() {
        final Type[] types = ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments();
        this.domainClass = (Class) types[0];
        this.entityClass = (Class) types[1];
        this.mapperClass = (Class) types[3];
    }

    /**
     * Gets an optional resource matching the provided id.
     *
     * @param id The id of the looked up resource.
     * @return An optional resource. Empty if it couldn't be found.
     */
    public abstract Optional<T> get(final Object id);

    /**
     * Gets a paginated response to the query.
     *
     * @param pagedQuery The information about the queried resource.
     * @return A paginated response.
     */
    public abstract PagedResponse<T> get(final PagedQuery<T> pagedQuery);

    public abstract Optional<T> patch(final Object id, final T domainObject, Object patch);

    public abstract T post(final T domainObject);

    public abstract T put(final Object id, final T domainObject);

    /**
     * Gets an instance of the mapper.
     *
     * @return An instance of the mapper.
     */
    private W getMapper() {
        if (this.mapper == null) {
            try {
                this.mapper = (W) Stream.of(this.mapperClass.getFields())
                        .filter(field -> field.getType().equals(this.mapperClass))
                        .findAny()
                        .orElseThrow(() -> {
                            final NoMapperInstanceFoundException noMapperInstanceFoundException = new NoMapperInstanceFoundException(this.mapperClass);
                            Logger.getLogger("DataAccess").log(Level.SEVERE,
                                    String.format("%s\nDetails: %s", noMapperInstanceFoundException.toString(), noMapperInstanceFoundException.getDetails())
                            );
                            return noMapperInstanceFoundException;
                        })
                        .get(null);
            } catch (final IllegalAccessException e) {
                final MapperIllegalAccessException mapperIllegalAccessException = new MapperIllegalAccessException(e, this.mapperClass);
                Logger.getLogger("DataAccess").log(Level.SEVERE,
                        String.format("%s\nDetails: %s", mapperIllegalAccessException.toString(), mapperIllegalAccessException.getDetails())
                );
                throw mapperIllegalAccessException;
            }
        }

        return this.mapper;
    }

    /**
     * Gets the page number set in the query.
     * <p>
     * If the page is not set, returns 1.
     *
     * @param pagedQuery The information about the query.
     * @return The page number.
     * @throws PagingException if the page number is less than 1.
     */
    protected Long getPage(final PagedQuery<T> pagedQuery) {
        final Long page = pagedQuery.getPage().orElse(1L);
        if (page < 1) {
            throw new PagingException("page", page);
        }

        return page;
    }

    /**
     * Gets the number of resources per page set in the query.
     * <p>
     * If the number of resources per page is not set, returns 1.
     *
     * @param pagedQuery The information about the query.
     * @return The number of resources per page.
     * @throws PagingException if the number of resources per page is less than 1.
     */
    protected Long getPerPage(final PagedQuery<T> pagedQuery) {
        final Long perPage = pagedQuery.getPerPage().orElse((long) Integer.MAX_VALUE);
        if (perPage < 1) {
            throw new PagingException("per_page", perPage);
        }

        return perPage;
    }

    /**
     * Maps an entity into a domain object.
     *
     * @param entity      The entity.
     * @param entityClass The entity class.
     * @param domainClass The domain class.
     * @return A mapped entity from the domain object.
     */
    protected T mapEntityToDomainObject(final U entity, final Class<U> entityClass, final Class<T> domainClass) {
        return (T) this.mapResources(Collections.singletonList(entity), entityClass, domainClass).get(0);
    }

    /**
     * Maps a domain object into an entity.
     *
     * @param domainObject The domain object.
     * @param domainClass  The domain class.
     * @param entityClass  The entity class.
     * @return A mapped domain object from the entity.
     */
    protected U mapDomainObjectToEntity(final T domainObject, final Class<T> domainClass, final Class<U> entityClass) {
        return (U) this.mapResources(Collections.singletonList(domainObject), domainClass, entityClass).get(0);
    }

    /**
     * Map a list of source objects into a list of target objects.
     *
     * @param sourceObjects The list of source objects.
     * @param sourceClass   The class of the source objects.
     * @param targetClass   The class of the target objects.
     * @return A list of target objects converted from the list of source objects.
     */
    protected List mapResources(final List<?> sourceObjects, final Class sourceClass, final Class targetClass) {
        final List resources = new ArrayList<>();
        final Method mapMethod = Stream.of(this.mapperClass.getMethods())
                .filter(method -> method.getReturnType().equals(targetClass))
                .filter(method -> method.getParameterTypes() != null && method.getParameterCount() == 1 && method.getParameterTypes()[0].equals(sourceClass))
                .findAny()
                .orElseThrow(() -> {
                    final NoMapMethodFoundException noMapMethodFoundException = new NoMapMethodFoundException(this.mapperClass, sourceClass, targetClass);
                    Logger.getLogger("DataAccess").log(Level.SEVERE,
                            String.format("%s\nDetails: %s", noMapMethodFoundException.toString(), noMapMethodFoundException.getDetails())
                    );
                    return noMapMethodFoundException;
                });

        sourceObjects.forEach(resource -> {
            try {
                resources.add(mapMethod.invoke(this.getMapper(), resource));
            } catch (final IllegalAccessException e) {
                final MapperIllegalAccessException mapperIllegalAccessException = new MapperIllegalAccessException(e, this.mapperClass, sourceClass, targetClass, mapMethod);
                Logger.getLogger("DataAccess").log(Level.SEVERE,
                        String.format("%s\nDetails: %s", mapperIllegalAccessException.toString(), mapperIllegalAccessException.getDetails())
                );
                throw mapperIllegalAccessException;
            } catch (final InvocationTargetException e) {
                final MapInvocationTargetException mapInvocationTargetException = new MapInvocationTargetException(e, this.mapperClass, sourceClass, targetClass, mapMethod);
                Logger.getLogger("DataAccess").log(Level.SEVERE,
                        String.format("%s\nDetails: %s", mapInvocationTargetException.toString(), mapInvocationTargetException.getDetails())
                );
                throw mapInvocationTargetException;
            }
        });

        return resources;
    }
}
