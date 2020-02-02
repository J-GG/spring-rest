package fr.jg.springrest.data.exceptions;

import java.lang.reflect.Field;
import java.util.Arrays;

public class IllegalAccessPruningException extends ServerException {

    public IllegalAccessPruningException(final Throwable cause, final Class domainObject, final Field field) {
        super("The field to prune is not accessible.", cause);
        this.details.put("domain_object", domainObject.getName());
        this.details.put("field", field.getName());
        this.details.put("cause", Arrays.toString(cause.getStackTrace()));
        this.details.put("how_to_solve", String.format("Make sure the field %s in %s can be modified by the pruning method.", field.getName(), domainObject.getSimpleName()));
    }
}
