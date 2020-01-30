package fr.jg.springrest;

import fr.jg.springrest.enumerations.SortingOrderEnum;
import fr.jg.springrest.exceptions.MapInvocationTargetException;
import fr.jg.springrest.exceptions.MapperIllegalAccessException;
import fr.jg.springrest.exceptions.NoMapMethodFoundException;
import fr.jg.springrest.exceptions.NoMapperInstanceFoundException;
import fr.jg.springrest.functional.DataAccess;
import fr.jg.springrest.functional.FilterableFieldFilter;
import fr.jg.springrest.functional.SortableFieldFilter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.SortedMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public interface SpecificationDataAccess<T, U, V extends JpaSpecificationExecutor<U>, W> extends DataAccess<T, U, V, W> {

    @Override
    default PagedResponse<T> get(final PagedResource<T> pagedResource, final V repository, final FilterableFieldFilter filterableFieldFilter, final SortableFieldFilter sortableFieldFilter) {
        final ParameterizedType dataAccessType = Arrays.stream(this.getClass().getGenericInterfaces())
                .filter(type -> type instanceof ParameterizedType)
                .map(type -> (ParameterizedType) type)
                .filter(parameterizedType -> parameterizedType.getRawType().getTypeName().equals("fr.jg.springrest.SpecificationDataAccess"))
                .findAny()
                .get();

        final Type[] types = dataAccessType.getActualTypeArguments();

        final Class domainClass = ((Class) types[0]);
        final Class entityClass = ((Class) types[1]);
        final Class mapperClass = ((Class) types[3]);

        Specification specification = null;
        final List<FilterCriteria> filterCriterias = pagedResource.getFiltersForClass(domainClass, filterableFieldFilter);
        for (final FilterCriteria filterCriteria : filterCriterias) {
            final FilterSpecification filterSpecification = new FilterSpecification<>(filterCriteria);
            specification = specification == null ? filterSpecification : specification.and(filterSpecification);
        }

        final SortedMap<String, SortingOrderEnum> sortedMap = pagedResource.getSortForClass(entityClass, sortableFieldFilter);
        final List<Sort.Order> orders = sortedMap
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

        final PageRequest pageRequest = PageRequest.of(pagedResource.getPage().orElse(0), pagedResource.getPerPage().orElse(10), Sort.by(orders));
        final Page companiesEntity = repository.findAll(specification, pageRequest);

        final List<T> resources = new ArrayList<>();
        try {
            final W mapper = (W) Stream.of(mapperClass.getFields())
                    .filter(field -> field.getType().equals(mapperClass))
                    .findAny()
                    .orElseThrow(() -> new NoMapperInstanceFoundException(String.format("No instance of the mapper %s could be found.", mapperClass)))
                    .get(null);

            final Method mapMethod = Stream.of(mapperClass.getMethods())
                    .filter(method -> method.getReturnType().equals(domainClass))
                    .filter(method -> method.getParameterTypes() != null && method.getParameterTypes()[0].equals(entityClass))
                    .findAny()
                    .orElseThrow(() -> new NoMapMethodFoundException(String.format("No method could be found to map from object %s to entity %s.", domainClass, entityClass)));
            companiesEntity.getContent().forEach(resource -> {
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

        return new PagedResponse<>(pagedResource,
                companiesEntity.getNumberOfElements(),
                companiesEntity.getTotalPages(),
                companiesEntity.getTotalElements(),
                resources);
    }
}
