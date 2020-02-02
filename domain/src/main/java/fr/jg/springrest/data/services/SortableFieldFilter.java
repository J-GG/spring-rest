package fr.jg.springrest.data.services;

import java.lang.reflect.Field;

public interface SortableFieldFilter {
    boolean filter(final String sort, Field field);
}
