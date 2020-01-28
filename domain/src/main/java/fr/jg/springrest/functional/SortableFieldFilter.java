package fr.jg.springrest.functional;

import java.lang.reflect.Field;

public interface SortableFieldFilter {
    boolean filter(final String sort, Field field);
}
