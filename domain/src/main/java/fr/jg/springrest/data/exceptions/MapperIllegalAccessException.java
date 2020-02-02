package fr.jg.springrest.data.exceptions;

import java.lang.reflect.Method;
import java.util.Arrays;

public class MapperIllegalAccessException extends ServerException {

    public MapperIllegalAccessException(final Throwable cause, final Class mapperClass, final Class domainClass, final Class entityClass) {
        super("Forbidden access to get the mapper instance to map a domain object to an entity object.", cause);
        this.details.put("mapper_class", mapperClass.getName());
        this.details.put("domain_object", domainClass.getName());
        this.details.put("entity_object", entityClass.getName());
        this.details.put("cause", Arrays.toString(cause.getStackTrace()));
        this.details.put("how_to_solve", "Make sure the invoking method can get the mapper instance.");
    }

    public MapperIllegalAccessException(final Throwable cause, final Class mapperClass, final Class domainClass, final Class entityClass, final Method mapMethod) {
        super("Forbidden access to the map method while trying to map a domain object to an entity object.", cause);
        this.details.put("mapper_class", mapperClass.getName());
        this.details.put("map_method", mapMethod.getName());
        this.details.put("domain_object", domainClass.getName());
        this.details.put("entity_object", entityClass.getName());
        this.details.put("cause", Arrays.toString(cause.getStackTrace()));
        this.details.put("how_to_solve", String.format("Make sure the invoking method can invoke the %s method.", mapMethod.getName()));
    }
}
