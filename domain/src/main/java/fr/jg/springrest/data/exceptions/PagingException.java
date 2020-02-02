package fr.jg.springrest.data.exceptions;

public class PagingException extends RestException {

    public PagingException(final String parameter, final Long value) {
        super("An error occurred due to malformed paging parameters.");
        this.details.put("parameter", parameter);
        this.details.put("expected", ">=0");
        this.details.put("actual", value);
    }
}
