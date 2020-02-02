package fr.jg.springrest.data.exceptions;

public class NoMapMethodFoundException extends ServerException {

    public NoMapMethodFoundException(final Class mapperClass, final Class domainClass, final Class entityClass) {
        super("No method could be found to map a domain object to an entity object.");
        this.details.put("mapper_class", mapperClass.getName());
        this.details.put("domain_object", domainClass.getName());
        this.details.put("entity_object", entityClass.getName());
        this.details.put("how_to_solve", String.format("Add a method in %s taking a %s parameter and returning a %s.",
                mapperClass.getSimpleName(),
                domainClass.getSimpleName(),
                entityClass.getSimpleName()
        ));
    }
}
