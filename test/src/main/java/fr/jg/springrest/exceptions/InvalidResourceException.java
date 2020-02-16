package fr.jg.springrest.exceptions;

import com.fasterxml.jackson.annotation.JsonProperty;
import fr.jg.springrest.data.exceptions.DetailedException;

import javax.validation.ConstraintViolation;
import java.lang.annotation.Annotation;
import java.util.*;
import java.util.stream.Collectors;

public class InvalidResourceException extends DetailedException {

    public InvalidResourceException(final String resource, final Set<ConstraintViolation<Object>> constraintViolations) {
        super("The data describing the resource isn't valid.");
        this.details.put("resource", resource);
        final List<ConstraintViolation<Object>> constraints = constraintViolations.stream().sorted((o1, o2) -> {
            final Integer nbPoints1 = o1.getPropertyPath().toString().length() - o1.getPropertyPath().toString().replace(".", "").length();
            final Integer nbPoints2 = o2.getPropertyPath().toString().length() - o2.getPropertyPath().toString().replace(".", "").length();
            return nbPoints1.compareTo(nbPoints2);
        }).collect(Collectors.toList());
        final List<Map<String, Object>> errors = new ArrayList<>();
        final Map<String, String> fields = new HashMap<>();
        for (final ConstraintViolation<Object> constraintViolation : constraints) {
            final String fullFieldName = constraintViolation.getPropertyPath().toString();
            String path = "";
            String jsonFieldName = fullFieldName;
            if (fullFieldName.contains(".")) {
                path = fullFieldName.substring(0, fullFieldName.lastIndexOf('.'));
                jsonFieldName = fullFieldName.substring(fullFieldName.lastIndexOf('.') + 1);
            }
            try {
                final Annotation annotation = constraintViolation.getLeafBean().getClass().getDeclaredField(jsonFieldName).getAnnotation(JsonProperty.class);
                if (annotation != null) {
                    jsonFieldName = ((JsonProperty) annotation).value();
                }
            } catch (final NoSuchFieldException e) {
                e.printStackTrace();
            }
            String fullJsonFieldName = jsonFieldName;
            if (fields.containsKey(path)) {
                fullJsonFieldName = jsonFieldName.concat(".").concat(fields.get(path));
            }

            fields.put(fullFieldName, fullJsonFieldName);

            final Map<String, Object> error = new LinkedHashMap<>();
            error.put("field", fullJsonFieldName);
            error.put("value", constraintViolation.getInvalidValue() != null ? constraintViolation.getInvalidValue().toString() : null);
            error.put("message", constraintViolation.getMessage());
            errors.add(error);
        }
        this.details.put("errors", errors);
        this.details.put("how_to_solve", "Make the specified correction for each error.");
    }
}
