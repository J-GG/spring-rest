package com.example.toolbox;

public class FilterCriteria {

    private final String field;

    private final FilterOperatorEnum operator;

    private final String value;

    private final String[] values;

    public FilterCriteria(final String field, final FilterOperatorEnum operator, final String value, final String[] values) {
        this.field = field;
        this.operator = operator;
        this.value = value;
        this.values = values;
    }

    public String getField() {
        return this.field;
    }

    public FilterOperatorEnum getOperator() {
        return this.operator;
    }

    public String getValue() {
        return this.value;
    }

    public String[] getValues() {
        return this.values;
    }
}
