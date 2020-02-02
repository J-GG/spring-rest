package fr.jg.springrest.data.enumerations;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum FilterOperatorEnum {

    EQUAL("eq", FilterOperatorExpectedValue.SINGLE),

    NOT_EQUAL("neq", FilterOperatorExpectedValue.SINGLE),

    GREATER_THAN("gt", FilterOperatorExpectedValue.SINGLE),

    GREATER_THAN_OR_EQUAL("gte", FilterOperatorExpectedValue.SINGLE),

    LESS_THAN("lt", FilterOperatorExpectedValue.SINGLE),

    LESS_THAN_OR_EQUAL("lte", FilterOperatorExpectedValue.SINGLE),

    IN("in", FilterOperatorExpectedValue.ARRAY),

    NOT_IN("nin", FilterOperatorExpectedValue.ARRAY),

    NULL("null", FilterOperatorExpectedValue.SINGLE),

    LIKE("like", FilterOperatorExpectedValue.SINGLE);

    private final String operator;

    private final FilterOperatorExpectedValue expectedValue;

    FilterOperatorEnum(final String operator, final FilterOperatorExpectedValue expectedValue) {
        this.operator = operator;
        this.expectedValue = expectedValue;
    }

    public static FilterOperatorEnum fromOperator(final String operator) {
        return Stream.of(FilterOperatorEnum.values())
                .filter(filterOperator -> filterOperator.operator.equals(operator))
                .findAny()
                .orElse(null);
    }

    public static List<FilterOperatorEnum> fromExpectedValue(final FilterOperatorExpectedValue expectedValue) {
        return Stream.of(FilterOperatorEnum.values())
                .filter(filterOperator -> filterOperator.expectedValue.equals(expectedValue))
                .collect(Collectors.toList());
    }

    public String getOperator() {
        return this.operator;
    }

    public FilterOperatorExpectedValue getExpectedValue() {
        return this.expectedValue;
    }
}
