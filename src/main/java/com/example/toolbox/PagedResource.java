package com.example.toolbox;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.lang.reflect.Field;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PagedResource<T> extends PartialResource<T> {

    private Integer page;

    private Integer perPage;

    private SortedMap<String, SortingOrderEnum> sort;

    private List<FilterCriteria> filters;

    private static final Pattern filterPattern = Pattern.compile(
            String.format("(?<field>[a-zA-Z_]+):(?<operator>%s):((?<value>[0-9a-zA-Z-]+)|(?<values>\\([0-9a-zA-Z,]+\\)))",
                    Stream.of(FilterOperatorEnum.values()).map(FilterOperatorEnum::getOperator).collect(Collectors.joining("|")))
    );

    public PagedResource() {
        this.sort = new TreeMap<>();
        this.filters = new ArrayList<>();
    }

    public PagedResource(final List<String> fields, final Integer page, final Integer perPage, final SortedMap<String, SortingOrderEnum> sort, final List<FilterCriteria> filters) {
        super(fields);
        this.page = page;
        this.perPage = perPage;
        this.sort = sort;
        this.filters = filters;
    }

    public Optional<Integer> getPage() {
        return Optional.ofNullable(this.page);
    }

    public void setPage(final Integer page) {
        this.page = page;
    }

    public Optional<Integer> getPerPage() {
        return Optional.ofNullable(this.perPage);
    }

    public void setPerPage(final Integer perPage) {
        this.perPage = perPage;
    }

    public SortedMap<String, SortingOrderEnum> getSort() {
        return this.sort;
    }

    public SortedMap<String, SortingOrderEnum> getSortForClass(final Class<T> clazz) {
        //Supprimer les entrées qui ne correspondent pas à un nom de propriété JSON de clazz ou à un nom d'attribut si il n'y a pas de propriété JSON
        //Si il y a une propriété JSON, remplacer le nom de la propriété par le nom de l'attribut (puis éventuellemnt plus tard par la valeur de l'annotation faisant référence à l'entity)

        final SortedMap<String, SortingOrderEnum> sortForClass = new TreeMap<>();

        for (final Map.Entry<String, SortingOrderEnum> entry : this.sort.entrySet()) {
            for (final Field field : clazz.getDeclaredFields()) {
                if (field.getAnnotation(JsonProperty.class) != null) {
                    if (entry.getKey().equals(field.getAnnotation(JsonProperty.class).value())) {
                        sortForClass.put(field.getName(), entry.getValue());
                    }
                } else {
                    if (entry.getKey().equals(field.getName())) {
                        sortForClass.put(field.getName(), entry.getValue());
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
                    }).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (firstOccurrence, secondOccurrence) -> firstOccurrence, TreeMap::new));
        } else {
            this.sort = new TreeMap<>();
        }
    }

    public List<FilterCriteria> getFilters() {
        return this.filters;
    }

    public List<FilterCriteria> getFiltersForClass(final Class<T> clazz) {
        final List<FilterCriteria> filtersForClass = new ArrayList<>();

        for (final FilterCriteria filterCriteria : this.filters) {
            for (final Field field : clazz.getDeclaredFields()) {
                if (field.getAnnotation(JsonProperty.class) != null) {
                    if (filterCriteria.getField().equals(field.getAnnotation(JsonProperty.class).value())) {
                        filtersForClass.add(new FilterCriteria(field.getName(), filterCriteria.getOperator(), filterCriteria.getValue(), filterCriteria.getValues()));
                    }
                } else {
                    if (filterCriteria.getField().equals(field.getName())) {
                        filtersForClass.add(new FilterCriteria(field.getName(), filterCriteria.getOperator(), filterCriteria.getValue(), filterCriteria.getValues()));
                    }
                }
            }
        }

        return filtersForClass;
    }

    public void setFilters(final String filters) {
        if (filters != null) {
            this.filters = Stream.of(filters.split(","))
                    .map(filterPattern::matcher)
                    .filter(Matcher::matches)
                    .map(matcher -> {
                        String[] values = null;
                        if (matcher.group("values") != null) {
                            values = matcher.group("values").substring(1, matcher.group("values").length() - 1).split(",");
                        }

                        return new FilterCriteria(matcher.group("field"),
                                FilterOperatorEnum.fromOperator(matcher.group("operator")),
                                matcher.group("value"),
                                values);
                    })
                    .collect(Collectors.toList());
        } else {
            this.filters = new ArrayList<>();
        }
    }
}
