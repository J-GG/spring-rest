package fr.jg.springrest.data.services;

import com.fasterxml.jackson.annotation.JsonProperty;
import fr.jg.springrest.data.pojo.FilterCriteria;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;

@Service
public class JacksonFilterableFieldFilter implements FilterableFieldFilter {
    @Override
    public boolean filter(final FilterCriteria filterCriteria, final Field field) {
        if (field.getAnnotation(JsonProperty.class) != null) {
            return filterCriteria.getField().equals(field.getAnnotation(JsonProperty.class).value());
        } else {
            return filterCriteria.getField().equals(field.getName());
        }
    }
}
