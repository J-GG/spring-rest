package fr.jg.springrest.data.exceptions;

public class NoMapperInstanceFoundException extends ServerException {

    public NoMapperInstanceFoundException(final Class mapperClass) {
        super("No instance of the mapper class could be found.");
        this.details.put("mapper_class", mapperClass.getName());
        this.details.put("how_to_solve", String.format("Add an attribute in %s containing an instance of the same class.", mapperClass.getSimpleName()));
    }
}
