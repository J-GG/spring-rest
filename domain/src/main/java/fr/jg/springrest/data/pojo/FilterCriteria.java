package fr.jg.springrest.data.pojo;

import fr.jg.springrest.data.enumerations.FilterOperatorEnum;

public class FilterCriteria {

    private String internalFieldName;

    private String externalFieldName;

    private final FilterOperatorEnum operator;

    private final String value;

    private final String[] values;

    public FilterCriteria(final String internalFieldName, final String externalFieldName, final FilterOperatorEnum operator, final String value, final String[] values) {
        this.internalFieldName = internalFieldName;
        this.externalFieldName = externalFieldName;
        this.operator = operator;
        this.value = value;
        this.values = values;
    }

    public void setInternalFieldName(final String internalFieldName) {
        this.internalFieldName = internalFieldName;
    }

    public String getInternalFieldName() {
        return this.internalFieldName;
    }

    public String getExternalFieldName() {
        return this.externalFieldName;
    }

    public void setExternalFieldName(final String externalFieldName) {
        this.externalFieldName = externalFieldName;
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
