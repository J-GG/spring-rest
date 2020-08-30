package fr.jg.springrest.data.services;

import fr.jg.springrest.data.enumerations.SortingOrderEnum;
import fr.jg.springrest.data.pojo.FilterCriterion;
import fr.jg.springrest.data.pojo.PagedQuery;
import fr.jg.springrest.data.pojo.PagedResponse;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import javax.persistence.Id;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * An implementation of the {@link DataAccess} with {@link Specification}.
 *
 * @param <T> The domain object.
 * @param <U> The entity.
 * @param <V> The repository.
 * @param <W> The mapper.
 */
public class SpecificationDataAccess<T, U, V extends JpaSpecificationExecutor<U> & JpaRepository<U, Object>, W> extends DataAccess<T, U, V, W> {

    /**
     * The repository.
     */
    @Autowired
    private V repository;

    /**
     * The field filter.
     */
    @Autowired
    private FieldFilter fieldFilter;

    @Override
    public Optional<T> get(final Object id) {
        final Optional<U> optionalEntity = this.repository.findById(id);
        return optionalEntity.map(entity -> this.mapEntityToDomainObject(entity, this.entityClass, this.domainClass));
    }

    @Override
    public PagedResponse<T> get(final PagedQuery<T> pagedQuery) {
        final Long page = this.getPage(pagedQuery);
        final Long perPage = this.getPerPage(pagedQuery);

        Specification<U> specification = null;
        final List<FilterCriterion> filterCriteria = pagedQuery.getFiltersForClass(this.domainClass, this.fieldFilter);
        for (final FilterCriterion filterCriterion : filterCriteria) {
            final SpecificationFilter<U> specificationFilter = new SpecificationFilter<>(filterCriterion);
            specification = specification == null ? specificationFilter : specification.and(specificationFilter);
        }

        final Map<String, SortingOrderEnum> sortMap = pagedQuery.getSortForClass(this.domainClass, this.fieldFilter);
        final List<Sort.Order> orders = sortMap
                .entrySet()
                .stream()
                .map(entry -> {
                    if (entry.getValue().equals(SortingOrderEnum.ASCENDING)) {
                        return Sort.Order.asc(entry.getKey());
                    } else {
                        return Sort.Order.desc(entry.getKey());
                    }
                }).collect(Collectors.toList());

        final PageRequest pageRequest = PageRequest.of(Math.toIntExact(page - 1), Math.toIntExact(perPage), Sort.by(orders));
        final Page<U> entities = this.repository.findAll(specification, pageRequest);

        final List<T> domainObjects = this.mapResources(entities.getContent(), this.entityClass, this.domainClass);

        return new PagedResponse<>(pagedQuery.getSort(), pagedQuery.getFilters(), page, perPage,
                (long) entities.getNumberOfElements(), (long) entities.getTotalPages(), entities.getTotalElements(), domainObjects);
    }

    @Override
    public T post(final T domainObject) {
        final U entity = this.repository.save(this.mapDomainObjectToEntity(domainObject, this.domainClass, this.entityClass));
        return this.mapEntityToDomainObject(entity, this.entityClass, this.domainClass);
    }

    @Override
    public Optional<T> patch(final Object id, final T domainObject, final Object patch) {
        Optional<T> resource = Optional.empty();
        final Optional<U> optionalEntity = this.repository.findById(id);
        if (optionalEntity.isPresent()) {
            final U entity = optionalEntity.get();
            final U mappedEntity = this.mapDomainObjectToEntity(domainObject, this.domainClass, this.entityClass);

            Arrays.stream(mappedEntity.getClass().getDeclaredFields())
                    .forEach(field -> {
                        try {
                            field.setAccessible(true);
                            final String setter = "set" + field.getName().substring(0, 1).toUpperCase() + field.getName().substring(1);
                            entity.getClass().getMethod(setter, field.getType()).invoke(entity, field.get(mappedEntity));
                        } catch (final IllegalAccessException e) {
                            e.printStackTrace();
                        } catch (final NoSuchMethodException e) {
                            e.printStackTrace();
                        } catch (final InvocationTargetException e) {
                            e.printStackTrace();
                        }
                    });

            try {
                resource = Optional.ofNullable(this.mapEntityToDomainObject(this.repository.save(entity), this.entityClass, this.domainClass));
            } catch (final ConstraintViolationException e) {
                e.printStackTrace();
            }
        }

        return resource;
    }

    @Override
    public T put(final Object id, final T domainObject) {
        final U entity = this.mapDomainObjectToEntity(domainObject, this.domainClass, this.entityClass);
        Arrays.stream(entity.getClass().getDeclaredFields())
                .filter(field -> field.getAnnotation(Id.class) != null)
                .findFirst()
                .ifPresent(field -> {
                    try {
                        field.setAccessible(true);
                        field.set(entity, id);
                    } catch (final IllegalAccessException e) {
                        e.printStackTrace();
                    }
                });
        final U savedEntity = this.repository.save(entity);
        return this.mapEntityToDomainObject(savedEntity, this.entityClass, this.domainClass);
    }
}
