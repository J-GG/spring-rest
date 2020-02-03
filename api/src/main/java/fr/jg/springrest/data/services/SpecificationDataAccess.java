package fr.jg.springrest.data.services;

import fr.jg.springrest.data.enumerations.SortingOrderEnum;
import fr.jg.springrest.data.pojo.FilterCriteria;
import fr.jg.springrest.data.pojo.PagedResource;
import fr.jg.springrest.data.pojo.PagedResponse;
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
    private FilterableFieldFilter filterableFieldFilter;

    @Autowired
    private SortableFieldFilter sortableFieldFilter;

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
    public PagedResponse<T> get(final PagedResource<T> pagedResource) {
        final Long page = this.getPage(pagedResource);
        final Long perPage = this.getPerPage(pagedResource);

        Specification specification = null;
        final List<FilterCriteria> filterCriterias = pagedResource.getFiltersForClass(this.domainClass, this.filterableFieldFilter);
        for (final FilterCriteria filterCriteria : filterCriterias) {
            final SpecificationFilter specificationFilter = new SpecificationFilter<>(filterCriteria);
            specification = specification == null ? specificationFilter : specification.and(specificationFilter);
        }

        final Map<String, SortingOrderEnum> sortMap = pagedResource.getSortForClass(this.domainClass, this.sortableFieldFilter);
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

        return new PagedResponse<>(pagedResource.getFields(), pagedResource.getSort(), pagedResource.getFilters(), page, perPage,
                (long) entities.getNumberOfElements(), (long) entities.getTotalPages(), entities.getTotalElements(), domainObjects);
    }

    @Override
    public Optional<T> patch(final UUID id, final T domainObject) {
        final Optional<U> optionalEntity = this.repository.findById(id);
        if (optionalEntity.isPresent()) {
            final U entity = optionalEntity.get();
            final U mappedEntity = this.mapDomainObjectToEntity(domainObject, this.domainClass, this.entityClass);

            Arrays.stream(mappedEntity.getClass().getDeclaredFields())
                    .filter(field -> {
                        field.setAccessible(true);
                        try {
                            return field.get(mappedEntity) != null;
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                        return false;
                    })
                    .forEach(field -> {
                        try {
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

            this.repository.save(entity);
            return Optional.ofNullable(this.mapEntityToDomainObject(entity, this.entityClass, this.domainClass));
        }

        return Optional.empty();
    }
}
