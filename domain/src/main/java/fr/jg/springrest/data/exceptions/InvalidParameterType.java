package fr.jg.springrest.data.exceptions;

public class InvalidParameterType extends DetailedException {

    public InvalidParameterType(final String field, final String value, final String expectedType) {
        super("The type of the parameter is incorrect.");
        this.details.put("field", field);
        this.details.put("value", value);
        this.details.put("expected_type", expectedType);
        this.details.put("how_to_solve", "Make sure the provided value matches the expected type.");
    }
}
