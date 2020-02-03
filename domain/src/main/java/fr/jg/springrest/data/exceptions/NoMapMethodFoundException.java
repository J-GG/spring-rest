package fr.jg.springrest.data.exceptions;

public class NoMapMethodFoundException extends ServerException {

    public NoMapMethodFoundException(final Class mapperClass, final Class sourceClass, final Class targetClass) {
        super("No method could be found to map an object to another.");
        this.details.put("mapper_class", mapperClass.getName());
        this.details.put("source_class", sourceClass.getName());
        this.details.put("target_class", targetClass.getName());
        this.details.put("how_to_solve", String.format("Add a method in %s taking a %s parameter and returning a %s.",
                mapperClass.getSimpleName(),
                sourceClass.getSimpleName(),
                targetClass.getSimpleName()
        ));
    }
}
