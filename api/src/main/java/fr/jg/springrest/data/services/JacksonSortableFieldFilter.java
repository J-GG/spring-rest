package fr.jg.springrest.data.services;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;

@Service
public class JacksonSortableFieldFilter implements FieldFilter {
    @Override
    public boolean filter(final String fieldName, final Field field) {
        if (field.getAnnotation(JsonProperty.class) != null) {
            return fieldName.equals(field.getAnnotation(JsonProperty.class).value());
        } else {
            return fieldName.equals(field.getName());
        }
    }
}
