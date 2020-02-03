package fr.jg.springrest.data.exceptions;

import java.lang.reflect.Method;
import java.util.Arrays;

public class MapperIllegalAccessException extends ServerException {

    public MapperIllegalAccessException(final Throwable cause, final Class mapperClass) {
        super("Forbidden access to get the mapper instance to map a domain object to an entity object and vice versa.", cause);
        this.details.put("mapper_class", mapperClass.getName());
        this.details.put("cause", Arrays.toString(cause.getStackTrace()));
        this.details.put("how_to_solve", "Make sure the invoking method can get the mapper instance.");
    }

    public MapperIllegalAccessException(final Throwable cause, final Class mapperClass, final Class sourceClass, final Class targetClass, final Method mapMethod) {
        super("Forbidden access to the map method while trying to map a domain object to an entity object and vice versa.", cause);
        this.details.put("mapper_class", mapperClass.getName());
        this.details.put("map_method", mapMethod.getName());
        this.details.put("source_class", sourceClass.getName());
        this.details.put("target_class", targetClass.getName());
        this.details.put("cause", Arrays.toString(cause.getStackTrace()));
        this.details.put("how_to_solve", String.format("Make sure the invoking method can invoke the %s method.", mapMethod.getName()));
    }
}
