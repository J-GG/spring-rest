package fr.jg.springrest.data.services;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;

@Service
public class FieldFilterImpl implements FieldFilter {
    @Override
    public boolean isExternalPathCorrect(final String fieldName, final Field field) {
        return (field.getAnnotation(JsonProperty.class) != null && field.getAnnotation(JsonProperty.class).value().equals(fieldName)) ||
                (((field.getAnnotation(JsonProperty.class) == null || field.getAnnotation(JsonProperty.class).value().isEmpty()) && field.getName().equals(fieldName)));
    }
}
