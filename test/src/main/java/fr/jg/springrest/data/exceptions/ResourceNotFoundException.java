package fr.jg.springrest.data.exceptions;

import java.util.UUID;

public class ResourceNotFoundException extends RestException {

    public ResourceNotFoundException(final String resource, final UUID id) {
        super("The requested resource couldn't be found");
        this.details.put("resource", resource);
        this.details.put("lookup_id", id);
    }
}
