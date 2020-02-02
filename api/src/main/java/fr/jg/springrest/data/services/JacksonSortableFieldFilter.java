package fr.jg.springrest.data.services;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;

@Service
public class JacksonSortableFieldFilter implements SortableFieldFilter {
    @Override
    public boolean filter(final String sort, final Field field) {
        if (field.getAnnotation(JsonProperty.class) != null) {
            return sort.equals(field.getAnnotation(JsonProperty.class).value());
        } else {
            return sort.equals(field.getName());
        }
    }
}
