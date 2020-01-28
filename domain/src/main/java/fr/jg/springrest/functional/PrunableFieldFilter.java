package fr.jg.springrest.functional;

import java.lang.reflect.Field;
import java.util.List;

public interface PrunableFieldFilter {
    boolean filter(List<String> fields, Field field);
}
