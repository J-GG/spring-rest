package com.example.toolbox;

import java.util.stream.Stream;

public enum FilterOperatorEnum {

    EQUAL("eq"),

    NOT_EQUAL("neq"),

    GREATER_THAN("gt"),

    GREATER_THAN_OR_EQUAL("gte"),

    LESS_THAN("lt"),

    LESS_THAN_OR_EQUAL("lte"),

    IN("in"),

    NOT_IN("nin"),

    NULL("null"),

    LIKE("like");

    private final String operator;

    FilterOperatorEnum(final String operator) {
        this.operator = operator;
    }

    public static FilterOperatorEnum fromOperator(final String operator) {
        return Stream.of(FilterOperatorEnum.values())
                .filter(filterOperator -> filterOperator.operator.equals(operator))
                .findAny()
                .orElse(null);
    }

    public String getOperator() {
        return this.operator;
    }
}
