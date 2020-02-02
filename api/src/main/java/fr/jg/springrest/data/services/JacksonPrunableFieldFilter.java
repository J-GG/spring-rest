package fr.jg.springrest.data.services;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.List;

@Service
public class JacksonPrunableFieldFilter implements PrunableFieldFilter {
    @Override
    public boolean filter(final List<String> fields, final Field field) {
        if (field.getAnnotation(JsonProperty.class) != null) {
            return !fields.contains(field.getAnnotation(JsonProperty.class).value());
        } else {
            return !fields.contains(field.getName());
        }
    }
}
