package fr.jg.springrest.data.pojo;

import fr.jg.springrest.data.annotations.Pageable;
import fr.jg.springrest.data.enumerations.FilterOperatorEnum;
import fr.jg.springrest.data.enumerations.FilterOperatorExpectedValue;
import fr.jg.springrest.data.enumerations.SortingOrderEnum;
import fr.jg.springrest.data.services.FieldFilter;

import java.lang.reflect.Field;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Contains the information of a REST query in order to page, sort and filter the resource.
 *
 * @param <T> The resource sent back to the client.
 */
public class PagedQuery<T> {
    /**
     * The page number.
     */
    private Long page;

    /**
     * The number of resources per page.
     */
    private Long perPage;

    /**
     * The map of fields based on which the resources should be sorted out.
     */
    private Map<String, SortingOrderEnum> sort;

    /**
     * The list of filter criteria to filter the list of resources.
     */
    private List<FilterCriterion> filters;

    /**
     * The regex to split the filters on comma not within parenthesis.
     */
    private static final String SPLIT_REGEX = ",(?![^\\(\\[]*[\\]\\)])";

    /**
     * The regex to extract the information about the filter from the URL (ex. field:eq:value or field:in:(val1,val2)).
     */
    private static final Pattern FILTER_PATTERN = Pattern.compile(
            String.format("(?<field>[a-zA-Z_\\.]+):(?:(?:(?<singleoperator>%s):(?<value>[0-9a-zA-ZÀ-ÿ-]+))|(?:(?<arrayoperator>%s):\\((?<values>[0-9a-zA-ZÀ-ÿ-,]+)\\)))",
                    FilterOperatorEnum.fromExpectedValue(FilterOperatorExpectedValue.SINGLE).stream().map(FilterOperatorEnum::getOperator).collect(Collectors.joining("|")),
                    FilterOperatorEnum.fromExpectedValue(FilterOperatorExpectedValue.MULTIPLE).stream().map(FilterOperatorEnum::getOperator).collect(Collectors.joining("|"))
            ));

    /**
     * Constructor.
     */
    public PagedQuery() {
        this.sort = new LinkedHashMap<>();
        this.filters = new ArrayList<>();
    }

    /**
     * Constructor.
     *
     * @param page    The page number.
     * @param perPage The number of resources per page.
     * @param sort    The map of fields based on which the resources should be sorted out.
     * @param filters The list of filters to filter the list of resources.
     */
    public PagedQuery(final Long page, final Long perPage, final Map<String, SortingOrderEnum> sort, final List<FilterCriterion> filters) {
        this.page = page;
        this.perPage = perPage;
        this.sort = sort;
        this.filters = filters;
    }

    /**
     * Gets an optional page number.
     *
     * @return An optional page number.
     */
    public Optional<Long> getPage() {
        return Optional.ofNullable(this.page);
    }

    /**
     * Sets the page number.
     *
     * @param page The page number.
     */
    public void setPage(final Long page) {
        this.page = page;
    }

    /**
     * Gets an optional number of resources per page.
     *
     * @return An optional number of resources per page.
     */
    public Optional<Long> getPerPage() {
        return Optional.ofNullable(this.perPage);
    }

    /**
     * Sets the number of resources per page.
     *
     * @param perPage The number of resources per page.
     */
    public void setPerPage(final Long perPage) {
        this.perPage = perPage;
    }

    /**
     * Gets the map of fields based on which the resources should be sorted out.
     * <p>
     * The field names are the unchanged ones provided to this class.
     *
     * @return The map of fields based on which the resources should be sorted out.
     */
    public Map<String, SortingOrderEnum> getSort() {
        return this.sort;
    }

    /**
     * Gets the map of fields based on which resources should be sorted.
     * <p>
     * The fields names are the actual internal names used for sorting.
     *
     * @param clazz       The class containing the fields.
     * @param fieldFilter The filter to determine whether the clazz contains a given field.
     * @return The map of fields based on which the resources should be sorted out.
     */
    public Map<String, SortingOrderEnum> getSortForClass(final Class<T> clazz, final FieldFilter fieldFilter) {
        final Map<String, SortingOrderEnum> sortForClass = new LinkedHashMap<>();

        for (final Map.Entry<String, SortingOrderEnum> entry : this.sort.entrySet()) {
            final List<String> fieldPath = Arrays.asList(entry.getKey().split("\\."));
            PagedQuery.getFullFieldName(true, fieldPath, clazz, fieldFilter).ifPresent(fullFieldName -> sortForClass.put(fullFieldName, entry.getValue()));
        }

        return sortForClass;
    }

    /**
     * Gets an optional full path representing the actual internal names based on the path provided to this class.
     * <p>
     * Is empty if the path is incorrect.
     *
     * @param isSortSearch Whether this method is called in the case of a sorting. If false, it is considered to be used for filtering.
     * @param path         A list of fields representing the path.
     * @param clazz        The class containing the first item of the path.
     * @param fieldFilter  The filter to determine whether the clazz contains a given field.
     * @return An optional String representing the actual internal names.
     */
    private static Optional<String> getFullFieldName(final boolean isSortSearch, final List<String> path, final Class clazz, final FieldFilter fieldFilter) {
        final Optional<String> fullFieldName;

        Field field = null;
        String fieldName = null;
        for (final Field f : clazz.getDeclaredFields()) {
            if (fieldFilter.isExternalPathCorrect(path.get(0), f)) {
                final Pageable pageable = f.getAnnotation(Pageable.class);
                if (pageable != null) {
                    if ((isSortSearch && pageable.sortable()) || (!isSortSearch && pageable.filterable())) {
                        field = f;
                        fieldName = !pageable.value().isEmpty() ? pageable.value() : f.getName();
                    }
                }
                break;
            }
        }

        if (field == null) {
            return Optional.empty();
        }

        if (path.size() > 1) {
            final Optional<String> childFieldName = getFullFieldName(isSortSearch, path.subList(1, path.size()), field.getType(), fieldFilter);

            if (!childFieldName.isPresent()) {
                return Optional.empty();
            }

            fullFieldName = Optional.of(fieldName.concat(".").concat(childFieldName.get()));
        } else {
            fullFieldName = Optional.of(fieldName);
        }

        return fullFieldName;
    }

    /**
     * Sets the map of fields based on which the resources should be sorted out.
     *
     * @param sort The map of fields based on which the resources should be sorted out.
     */
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

    /**
     * Gets the list of filters to filter the list of resources.
     * <p>
     * The field names are the unchanged ones provided to this class.
     *
     * @return The list of filters to filter the list of resources.
     */
    public List<FilterCriterion> getFilters() {
        return this.filters;
    }

    /**
     * Gets the list of filter criteria based on which resources should be filtered.
     * <p>
     * The internal fields names are the actual internal names used for filtering.
     *
     * @param clazz       The class containing the fields.
     * @param fieldFilter The filter to determine whether the clazz contains a given field.
     * @return The list of filter criteria based on which resources should be filtered.
     */
    public List<FilterCriterion> getFiltersForClass(final Class<T> clazz, final FieldFilter fieldFilter) {
        final List<FilterCriterion> filtersForClass = new ArrayList<>();

        for (final FilterCriterion filterCriterion : this.filters) {
            final List<String> fieldPath = Arrays.asList(filterCriterion.getExternalFieldName().split("\\."));
            PagedQuery.getFullFieldName(false, fieldPath, clazz, fieldFilter).ifPresent(fullFieldName -> {
                filterCriterion.setInternalFieldName(fullFieldName);
                filtersForClass.add(filterCriterion);
            });
        }

        return filtersForClass;
    }

    /**
     * Sets the list of filters to filter the list of resources.
     *
     * @param filters The list of filters to filter the list of resources.
     */
    public void setFilters(final String filters) {
        if (filters != null) {
            this.filters = Stream.of(filters.split(SPLIT_REGEX))
                    .map(FILTER_PATTERN::matcher)
                    .filter(Matcher::matches)
                    .map(matcher -> new FilterCriterion(matcher.group("field"), matcher.group("field"),
                            matcher.group("singleoperator") != null
                                    ? FilterOperatorEnum.fromOperator(matcher.group("singleoperator")).orElse(null)
                                    : FilterOperatorEnum.fromOperator(matcher.group("arrayoperator")).orElse(null),
                            matcher.group("value"),
                            matcher.group("values") == null ? null : matcher.group("values").split(","))
                    ).collect(Collectors.toList());
        } else {
            this.filters = new ArrayList<>();
        }
    }

}
