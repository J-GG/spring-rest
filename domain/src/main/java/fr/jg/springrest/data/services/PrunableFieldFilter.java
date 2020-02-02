package fr.jg.springrest.data.services;

import java.lang.reflect.Field;
import java.util.List;

public interface PrunableFieldFilter {
    boolean filter(List<String> fields, Field field);
}
