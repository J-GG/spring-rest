package fr.jg.springrest.data.services;

import java.lang.reflect.Field;

public interface FieldFilter {
    boolean filter(final String fieldName, Field field);
}