package fr.jg.springrest.data.exceptions;

import fr.jg.springrest.errors.pojo.DetailedException;

/**
 * Exception relative to an invalid parameter type when another type was expected.
 */
public class InvalidParameterType extends DetailedException {
    /**
     * Constructor.
     *
     * @param message      The message about the exception.
     * @param field        The name of the field.
     * @param value        The value of the field.
     * @param expectedType The expected type of the field.
     */
    public InvalidParameterType(final String message, final String field, final String value, final String expectedType) {
        super(message);
        this.details.put("field", field);
        this.details.put("value", value);
        this.details.put("expected_type", expectedType);
    }
}
