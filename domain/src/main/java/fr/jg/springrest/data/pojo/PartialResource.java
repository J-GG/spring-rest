package fr.jg.springrest.data.pojo;

import fr.jg.springrest.data.exceptions.IllegalAccessPruningException;
import fr.jg.springrest.data.services.PrunableFieldFilter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PartialResource<T> {

    private List<String> fields;

    public PartialResource() {
        this.fields = new ArrayList<>();
    }

    public PartialResource(final String fields) {
        this.setFields(fields);
    }

    public PartialResource(final List<String> fields) {
        this.fields = fields;
    }

    public T prune(final T object, final PrunableFieldFilter prunableFieldFilter) {
        if (!this.fields.isEmpty()) {
            Stream.of(object.getClass().getDeclaredFields())
                    .filter(field -> prunableFieldFilter.filter(this.fields, field))
                    .forEach(field -> {
                        field.setAccessible(true);
                        try {
                            field.set(object, null);
                        } catch (final IllegalAccessException e) {
                            final IllegalAccessPruningException illegalAccessPruningException = new IllegalAccessPruningException(e, object.getClass(), field);
                            Logger.getLogger("DataAccess").log(Level.SEVERE,
                                    String.format("%s\nDetails: %s", illegalAccessPruningException.toString(), illegalAccessPruningException.getDetails())
                            );
                            throw illegalAccessPruningException;
                        }
                    });
        }

        return object;
    }

    public List<T> prune(final List<T> objectList, final PrunableFieldFilter pruneFieldsFilter) {
        return objectList.stream().map(object -> this.prune(object, pruneFieldsFilter)).collect(Collectors.toList());
    }

    public List<String> getFields() {
        return this.fields;
    }

    public void setFields(final String fields) {
        this.fields = fields != null ? Arrays.asList(fields.split(",")) : new ArrayList<>();
    }
}
