package fr.jg.springrest.data.exceptions;

import java.lang.reflect.Method;
import java.util.Arrays;

public class MapInvocationTargetException extends ServerException {

    public MapInvocationTargetException(final Throwable cause, final Class mapperClass, final Class domainClass, final Class entityClass, final Method mapMethod) {
        super("An exception occurred in the map method while mapping a domain object to an entity object", cause);
        this.details.put("mapper_class", mapperClass.getName());
        this.details.put("map_method", mapMethod.getName());
        this.details.put("domain_object", domainClass.getName());
        this.details.put("entity_object", entityClass.getName());
        this.details.put("cause", Arrays.toString(cause.getStackTrace()));
        this.details.put("how_to_solve", String.format("Check the origin of the error in the %s method of the %s class.", mapMethod.getName(), mapperClass.getSimpleName()));
    }
}
