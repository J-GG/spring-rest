package fr.jg.springrest.data.pojo;

import fr.jg.springrest.data.enumerations.FilterOperatorEnum;

/**
 * Represents the criterion of a filter.
 */
public class FilterCriterion {
    /**
     * The field name used internally.
     * <p>
     * It represents the path used by the entity class.
     */
    private String internalFieldName;

    /**
     * The field name used externally.
     * <p>
     * It represents the path used by the domain object.
     */
    private String externalFieldName;

    /**
     * The operator.
     */
    private final FilterOperatorEnum operator;

    /**
     * The single value.
     */
    private final String value;

    /**
     * The multiple values.
     */
    private final String[] values;

    /**
     * Constructor.
     *
     * @param internalFieldName The field name used internally.
     * @param externalFieldName The field name used externally.
     * @param operator          The operator.
     * @param value             The single value.
     * @param values            The multiple values.
     */
    public FilterCriterion(final String internalFieldName, final String externalFieldName, final FilterOperatorEnum operator, final String value, final String[] values) {
        this.internalFieldName = internalFieldName;
        this.externalFieldName = externalFieldName;
        this.operator = operator;
        this.value = value;
        this.values = values;
    }

    /**
     * Sets the field name used internally.
     *
     * @param internalFieldName The field name used internally.
     */
    public void setInternalFieldName(final String internalFieldName) {
        this.internalFieldName = internalFieldName;
    }

    /**
     * Gets the field name used internally.
     *
     * @return The field name used internally.
     */
    public String getInternalFieldName() {
        return this.internalFieldName;
    }

    /**
     * Gets the field name used externally.
     *
     * @return The field name used externally.
     */
    public String getExternalFieldName() {
        return this.externalFieldName;
    }

    /**
     * Sets the field name used externally
     *
     * @param externalFieldName The field name used externally.
     */
    public void setExternalFieldName(final String externalFieldName) {
        this.externalFieldName = externalFieldName;
    }

    /**
     * Gets the operator.
     *
     * @return The operator.
     */
    public FilterOperatorEnum getOperator() {
        return this.operator;
    }

    /**
     * Gets the single value.
     *
     * @return The single value.
     */
    public String getValue() {
        return this.value;
    }

    /**
     * Gets the multiple values.
     *
     * @return The multiple values.
     */
    public String[] getValues() {
        return this.values;
    }
}
