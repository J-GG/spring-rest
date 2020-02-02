package fr.jg.springrest.data.pojo;

import fr.jg.springrest.data.annotations.Pageable;
import fr.jg.springrest.data.enumerations.FilterOperatorEnum;
import fr.jg.springrest.data.enumerations.FilterOperatorExpectedValue;
import fr.jg.springrest.data.enumerations.SortingOrderEnum;
import fr.jg.springrest.data.services.FilterableFieldFilter;
import fr.jg.springrest.data.services.SortableFieldFilter;

import java.lang.reflect.Field;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PagedResource<T> extends PartialResource<T> {

    private Long page;

    private Long per_page;

    private Map<String, SortingOrderEnum> sort;

    private List<FilterCriteria> filters;

    private static final Pattern FILTER_PATTERN = Pattern.compile(
            String.format("(?<field>[a-zA-Z_]+):(?:(?:(?<singleoperator>%s):(?<value>[0-9a-zA-Z-]+))|(?:(?<arrayoperator>%s):\\((?<values>[0-9a-zA-Z,]+)\\)))",
                    FilterOperatorEnum.fromExpectedValue(FilterOperatorExpectedValue.SINGLE).stream().map(FilterOperatorEnum::getOperator).collect(Collectors.joining("|")),
                    FilterOperatorEnum.fromExpectedValue(FilterOperatorExpectedValue.ARRAY).stream().map(FilterOperatorEnum::getOperator).collect(Collectors.joining("|"))
            ));

    public PagedResource() {
        this.sort = new LinkedHashMap<>();
        this.filters = new ArrayList<>();
    }

    public PagedResource(final List<String> fields, final Long page, final Long perPage, final Map<String, SortingOrderEnum> sort, final List<FilterCriteria> filters) {
        super(fields);
        this.page = page;
        this.per_page = perPage;
        this.sort = sort;
        this.filters = filters;
    }

    public Optional<Long> getPage() {
        return Optional.ofNullable(this.page);
    }

    public void setPage(final Long page) {
        this.page = page;
    }

    public Optional<Long> getPer_page() {
        return Optional.ofNullable(this.per_page);
    }

    public void setPer_page(final Long per_page) {
        this.per_page = per_page;
    }

    public Map<String, SortingOrderEnum> getSort() {
        return this.sort;
    }

    public Map<String, SortingOrderEnum> getSortForClass(final Class<T> clazz, final SortableFieldFilter sortableFieldFilter) {
        final Map<String, SortingOrderEnum> sortForClass = new LinkedHashMap<>();

        for (final Map.Entry<String, SortingOrderEnum> entry : this.sort.entrySet()) {
            for (final Field field : clazz.getDeclaredFields()) {
                if (sortableFieldFilter.filter(entry.getKey(), field)) {
                    sortForClass.put(field.getName(), entry.getValue());
                }
            }
        }

        for (final Field field : clazz.getDeclaredFields()) {
            if (sortForClass.keySet().contains(field.getName())) {
                final Pageable pageable = field.getAnnotation(Pageable.class);
                if (pageable == null || !pageable.sortable()) {
                    sortForClass.remove(field.getName());
                } else {
                    if (!pageable.value().isEmpty() && !pageable.value().equals(field.getName())) {
                        sortForClass.put(pageable.value(), sortForClass.get(field.getName()));
                        sortForClass.remove(field.getName());
                    }
                }
            }
        }

        return sortForClass;
    }

    public void setSort(final String sort) {
        if (sort != null) {
            this.sort = Stream.of(sort.split(","))
                    .map(item -> {
                        final AbstractMap.SimpleEntry<String, SortingOrderEnum> entry;
                        if (item.startsWith("-")) {
                            entry = new AbstractMap.SimpleEntry<>(item.substring(1), SortingOrderEnum.DESCENDING);
                        } else {
                            entry = new AbstractMap.SimpleEntry<>(sort.startsWith("+") ? item.substring(1) : item, SortingOrderEnum.ASCENDING);
                        }

                        return entry;
                    }).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (firstOccurrence, secondOccurrence) -> firstOccurrence, LinkedHashMap::new));
        } else {
            this.sort = new LinkedHashMap<>();
        }
    }

    public List<FilterCriteria> getFilters() {
        return this.filters;
    }

    public List<FilterCriteria> getFiltersForClass(final Class<T> clazz, final FilterableFieldFilter filterableFieldFilter) {
        final List<FilterCriteria> filtersForClass = new ArrayList<>();

        for (final FilterCriteria filterCriteria : this.filters) {
            for (final Field field : clazz.getDeclaredFields()) {
                if (filterableFieldFilter.filter(filterCriteria, field)) {
                    filtersForClass.add(new FilterCriteria(field.getName(), filterCriteria.getOperator(), filterCriteria.getValue(), filterCriteria.getValues()));
                }
            }
        }

        final List<FilterCriteria> filterCriteriaToBeRemoved = new ArrayList<>();
        for (final Field field : clazz.getDeclaredFields()) {
            filtersForClass
                    .forEach(filterCriteria -> {
                        if (filterCriteria.getField().equals(field.getName())) {
                            final Pageable pageable = field.getAnnotation(Pageable.class);
                            if (pageable == null || !pageable.filterable()) {
                                filterCriteriaToBeRemoved.add(filterCriteria);
                            } else {
                                if (!pageable.value().isEmpty() && !pageable.value().equals(field.getName())) {
                                    filterCriteria.setField(pageable.value());
                                }
                            }

                        }
                    });
        }
        filtersForClass.removeAll(filterCriteriaToBeRemoved);

        return filtersForClass;
    }

    public void setFilters(final String filters) {
        if (filters != null) {
            this.filters = Stream.of(filters.split(","))
                    .map(FILTER_PATTERN::matcher)
                    .filter(Matcher::matches)
                    .map(matcher -> new FilterCriteria(matcher.group("field"),
                            matcher.group("singleoperator") != null ?
                                    FilterOperatorEnum.fromOperator(matcher.group("singleoperator")) :
                                    FilterOperatorEnum.fromOperator(matcher.group("arrayoperator")),
                            matcher.group("value"),
                            matcher.group("values") == null ? null : matcher.group("values").split(","))
                    ).collect(Collectors.toList());
        } else {
            this.filters = new ArrayList<>();
        }
    }

}
