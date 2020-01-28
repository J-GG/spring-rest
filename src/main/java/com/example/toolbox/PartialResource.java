package com.example.toolbox;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PartialResource<T> {

    private static final Logger logger = LoggerFactory.getLogger(PartialResource.class);

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

    public T prune(final T object) {
        if (!this.fields.isEmpty()) {
            Stream.of(object.getClass().getDeclaredFields())
                    .filter(field -> {
                        if (field.getAnnotation(JsonProperty.class) != null) {
                            return !this.fields.contains(field.getAnnotation(JsonProperty.class).value());
                        } else {
                            return !this.fields.contains(field.getName());
                        }
                    })
                    .forEach(field -> {
                        field.setAccessible(true);
                        try {
                            field.set(object, null);
                        } catch (final IllegalAccessException e) {
                            logger.info("The field %s could not be accessed on %s while trying to filter.", field.getName(), object.getClass().getName());
                        }
                    });
        }

        return object;
    }

    public List<T> prune(final List<T> objectList) {
        return objectList.stream().map(this::prune).collect(Collectors.toList());
    }

    public List<String> getFields() {
        return this.fields;
    }

    public void setFields(final String fields) {
        this.fields = fields != null ? Arrays.asList(fields.split(",")) : new ArrayList<>();
    }
}
