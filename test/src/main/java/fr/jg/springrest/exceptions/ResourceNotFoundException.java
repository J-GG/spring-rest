package fr.jg.springrest.exceptions;

import fr.jg.springrest.data.exceptions.DetailedException;

import java.util.UUID;

public class ResourceNotFoundException extends DetailedException {

    public ResourceNotFoundException(final String resource, final UUID id) {
        super("The requested resource couldn't be found");
        this.details.put("resource", resource);
        this.details.put("lookup_id", id);
        this.details.put("how_to_solve", "Make sure the id is correct.");
    }
}
