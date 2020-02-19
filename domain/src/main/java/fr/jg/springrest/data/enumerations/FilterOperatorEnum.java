package fr.jg.springrest.data.enumerations;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * The available operator to filter fields.
 * Each operator is defined as expecting either a single or multiple values.
 */
public enum FilterOperatorEnum {

    /**
     * The 'equal' operator.
     */
    EQUAL("eq", FilterOperatorExpectedValue.SINGLE),

    /**
     * The 'not equal' operator.
     */
    NOT_EQUAL("neq", FilterOperatorExpectedValue.SINGLE),

    /**
     * The 'greater than' operator.
     */
    GREATER_THAN("gt", FilterOperatorExpectedValue.SINGLE),

    /**
     * The 'greater than or equal' operator.
     */
    GREATER_THAN_OR_EQUAL("gte", FilterOperatorExpectedValue.SINGLE),

    /**
     * The 'less than' operator.
     */
    LESS_THAN("lt", FilterOperatorExpectedValue.SINGLE),

    /**
     * The 'less than or equal' operator.
     */
    LESS_THAN_OR_EQUAL("lte", FilterOperatorExpectedValue.SINGLE),

    /**
     * The 'null' operator.
     * <p>
     * Filter based on whether the field is null or not.
     */
    NULL("null", FilterOperatorExpectedValue.SINGLE),

    /**
     * The 'like' operator.
     * <p>
     * Filter based on whether the field contains a value or not.
     */
    LIKE("like", FilterOperatorExpectedValue.SINGLE),

    /**
     * The 'bool' operator.
     * <p>
     * Filter based on whether the field is true or false.
     */
    BOOL("bool", FilterOperatorExpectedValue.SINGLE),

    /**
     * The 'in' operator.
     * <p>
     * Filter based on whether the field contains any of the values.
     */
    IN("in", FilterOperatorExpectedValue.MULTIPLE),

    /**
     * The 'not in' operator.
     * <p>
     * Filter based on whether the field contains non of the values.
     */
    NOT_IN("nin", FilterOperatorExpectedValue.MULTIPLE);

    /**
     * The code of the operator.
     */
    private final String operator;

    /**
     * The type of values accepted by the operator.
     */
    private final FilterOperatorExpectedValue expectedValue;

    /**
     * Constructor.
     *
     * @param operator      The code of the operator.
     * @param expectedValue The type of values accepted by the operator.
     */
    FilterOperatorEnum(final String operator, final FilterOperatorExpectedValue expectedValue) {
        this.operator = operator;
        this.expectedValue = expectedValue;
    }

    /**
     * Gets the optional operator matching the code of the operator.
     *
     * @param operator The code of the operator.
     * @return the optional operator matching the code of the operator.
     */
    public static Optional<FilterOperatorEnum> fromOperator(final String operator) {
        return Stream.of(FilterOperatorEnum.values())
                .filter(filterOperator -> filterOperator.operator.equals(operator))
                .findAny();
    }

    /**
     * Gets the list of operators accepting the type of expected value.
     *
     * @param expectedValue The expected value accepted by the operator.
     * @return The list of operators accepting the type of expected value.
     */
    public static List<FilterOperatorEnum> fromExpectedValue(final FilterOperatorExpectedValue expectedValue) {
        return Stream.of(FilterOperatorEnum.values())
                .filter(filterOperator -> filterOperator.expectedValue.equals(expectedValue))
                .collect(Collectors.toList());
    }

    /**
     * Gets the code of the operator.
     *
     * @return The code of the operator.
     */
    public String getOperator() {
        return this.operator;
    }
}
