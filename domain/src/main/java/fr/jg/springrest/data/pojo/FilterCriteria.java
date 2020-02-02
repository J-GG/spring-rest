package fr.jg.springrest.data.pojo;

import fr.jg.springrest.data.enumerations.FilterOperatorEnum;

public class FilterCriteria {

    private String field;

    private final FilterOperatorEnum operator;

    private final String value;

    private final String[] values;

    public FilterCriteria(final String field, final FilterOperatorEnum operator, final String value, final String[] values) {
        this.field = field;
        this.operator = operator;
        this.value = value;
        this.values = values;
    }

    public void setField(final String field) {
        this.field = field;
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
