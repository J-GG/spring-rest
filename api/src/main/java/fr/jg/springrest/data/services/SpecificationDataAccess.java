package fr.jg.springrest.data.services;

import fr.jg.springrest.data.enumerations.SortingOrderEnum;
import fr.jg.springrest.data.pojo.FilterCriteria;
import fr.jg.springrest.data.pojo.PagedQuery;
import fr.jg.springrest.data.pojo.PagedResponse;
import fr.jg.springrest.data.pojo.PutResponse;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SpecificationDataAccess<T, U, V extends JpaSpecificationExecutor<U> & JpaRepository<U, UUID>, W> extends DataAccess<T, U, V, W> {

    private final Class<T> domainClass;

    private final Class<U> entityClass;

    private final Class<W> mapperClass;

    @Autowired
    private V repository;

    @Autowired
    private FieldFilter fieldFilter;

    public SpecificationDataAccess() {
        final Type[] types = ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments();
        this.domainClass = (Class) types[0];
        this.entityClass = (Class) types[1];
        this.mapperClass = (Class) types[3];
    }

    @Override
    protected Class<W> getMapperClass() {
        return this.mapperClass;
    }

    @Override
    public PagedResponse<T> get(final PagedQuery<T> pagedQuery) {
        final Long page = this.getPage(pagedQuery);
        final Long perPage = this.getPerPage(pagedQuery);

        Specification specification = null;
        final List<FilterCriteria> filterCriterias = pagedQuery.getFiltersForClass(this.domainClass, this.fieldFilter);
        for (final FilterCriteria filterCriteria : filterCriterias) {
            final SpecificationFilter specificationFilter = new SpecificationFilter<>(filterCriteria);
            specification = specification == null ? specificationFilter : specification.and(specificationFilter);
        }

        final Map<String, SortingOrderEnum> sortMap = pagedQuery.getSortForClass(this.domainClass, this.fieldFilter);
        final List<Sort.Order> orders = sortMap
                .entrySet()
                .stream()
                .filter(entry -> Stream.of(this.entityClass.getDeclaredFields()).anyMatch(field -> {
                    field.setAccessible(true);
                    return field.getName().equals(entry.getKey());
                }))
                .map(entry -> {
                    if (entry.getValue().equals(SortingOrderEnum.ASCENDING)) {
                        return Sort.Order.asc(entry.getKey());
                    } else {
                        return Sort.Order.desc(entry.getKey());
                    }
                }).collect(Collectors.toList());

        final PageRequest pageRequest = PageRequest.of(Math.toIntExact(page), Math.toIntExact(perPage), Sort.by(orders));
        final Page entities = this.repository.findAll(specification, pageRequest);

        final List<T> domainObjects = this.mapResources(entities.getContent(), this.entityClass, this.domainClass);

        return new PagedResponse<>(pagedQuery.getSort(), pagedQuery.getFilters(), page, perPage,
                (long) entities.getNumberOfElements(), (long) entities.getTotalPages(), entities.getTotalElements(), domainObjects);
    }

    @Override
    public Optional<T> patch(final UUID id, final T domainObject) {
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
    public PutResponse<T> put(final UUID id, final T domainObject) {
        return null;
    }
}
