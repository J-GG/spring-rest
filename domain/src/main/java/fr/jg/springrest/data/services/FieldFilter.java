package fr.jg.springrest.data.services;

import java.lang.reflect.Field;

/**
 * Interface used to define whether a string field name refers to a provided field.
 */
public interface FieldFilter {
    /**
     * Whether the string name of the field refers to the provided field.
     *
     * @param fieldName The string name of the field.
     * @param field     The compared field.
     * @return True if the string name refers to the field.
     */
    boolean isExternalPathCorrect(final String fieldName, Field field);
}
