package fr.jg.springrest.functional;

import fr.jg.springrest.FilterCriteria;

import java.lang.reflect.Field;

public interface FilterableFieldFilter {
    boolean filter(final FilterCriteria filterCriteria, Field field);
}
