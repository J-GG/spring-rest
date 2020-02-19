package fr.jg.springrest.data.exceptions;

import fr.jg.springrest.data.utility.FilterConverter;

/**
 * Exception relative to {@link FilterConverter}.
 */
public class FilterConverterException extends RuntimeException {
    /**
     * Constructor.
     *
     * @param message The message about the exception.
     **/
    public FilterConverterException(final String message) {
        super(message);
    }
}
