package fr.jg.springrest.data.services;

import fr.jg.springrest.data.pojo.FilterCriteria;

import java.lang.reflect.Field;

public interface FilterableFieldFilter {
    boolean filter(final FilterCriteria filterCriteria, Field field);
}
