package fr.jg.springrest.data.services;

import fr.jg.springrest.data.enumerations.SortingOrderEnum;
import fr.jg.springrest.data.pojo.FilterCriteria;
import fr.jg.springrest.data.pojo.PagedResource;
import fr.jg.springrest.data.pojo.PagedResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Service;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public interface SpecificationDataAccess<T, U, V extends JpaSpecificationExecutor<U>, W> extends DataAccess<T, U, V, W> {

    @Override
    default PagedResponse<T> get(final PagedResource<T> pagedResource, final V repository, final FilterableFieldFilter filterableFieldFilter, final SortableFieldFilter sortableFieldFilter) {
        final Long page = this.getPage(pagedResource);
        final Long perPage = this.getPerPage(pagedResource);

        final ParameterizedType dataAccessType = Arrays.stream(this.getClass().getGenericInterfaces())
                .filter(type -> type instanceof ParameterizedType)
                .map(type -> (ParameterizedType) type)
                .filter(parameterizedType -> parameterizedType.getRawType().getTypeName().equals("fr.jg.springrest.data.services.SpecificationDataAccess"))
                .findAny()
                .get();

        final Type[] types = dataAccessType.getActualTypeArguments();
        final Class domainClass = ((Class) types[0]);
        final Class entityClass = ((Class) types[1]);
        final Class mapperClass = ((Class) types[3]);

        Specification specification = null;
        final List<FilterCriteria> filterCriterias = pagedResource.getFiltersForClass(domainClass, filterableFieldFilter);
        for (final FilterCriteria filterCriteria : filterCriterias) {
            final SpecificationFilter specificationFilter = new SpecificationFilter<>(filterCriteria);
            specification = specification == null ? specificationFilter : specification.and(specificationFilter);
        }

        final Map<String, SortingOrderEnum> sortMap = pagedResource.getSortForClass(domainClass, sortableFieldFilter);
        final List<Sort.Order> orders = sortMap
                .entrySet()
                .stream()
                .filter(entry -> Stream.of(entityClass.getDeclaredFields()).anyMatch(field -> {
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
        final Page resourceEntities = repository.findAll(specification, pageRequest);

        final List<T> resources = this.mapResource(resourceEntities.getContent(), domainClass, entityClass, mapperClass);

        return new PagedResponse<>(pagedResource.getFields(), pagedResource.getSort(), pagedResource.getFilters(), page, perPage,
                (long) resourceEntities.getNumberOfElements(), (long) resourceEntities.getTotalPages(), resourceEntities.getTotalElements(), resources);
    }
}
