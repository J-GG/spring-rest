package fr.jg.springrest.data.exceptions;

public class PagingException extends DetailedException {

    public PagingException(final String parameter, final Long value) {
        super("An error occurred due to malformed paging.");
        this.details.put("parameter", parameter);
        this.details.put("expected", ">=0");
        this.details.put("actual", value);
        this.details.put("how_to_solve", String.format("You must specify a %s value equal or greater than 0.", parameter));
    }
}
