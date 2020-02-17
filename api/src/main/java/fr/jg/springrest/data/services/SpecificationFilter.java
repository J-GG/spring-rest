package fr.jg.springrest.data.services;

import fr.jg.springrest.data.enumerations.FilterOperatorEnum;
import fr.jg.springrest.data.exceptions.FilterConverterException;
import fr.jg.springrest.data.exceptions.InvalidParameterType;
import fr.jg.springrest.data.pojo.FilterCriteria;
import fr.jg.springrest.data.toolbox.FilterConverter;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SpecificationFilter<T> implements Specification<T> {

    private final FilterCriteria filterCriteria;

    public SpecificationFilter() {
        this.filterCriteria = null;
    }

    public SpecificationFilter(final FilterCriteria filterCriteria) {
        this.filterCriteria = filterCriteria;
    }

    public static Path fieldPath(final Root root, final Join join, final List<String> pathFields) {
        if (pathFields.size() > 1) {
            Join newJoin = join;
            if (join == null) {
                newJoin = root.join(pathFields.get(0));
            } else {
                newJoin = join.join(pathFields.get(0));
            }
            return fieldPath(root, newJoin, pathFields.subList(1, pathFields.size()));
        } else {
            if (join == null) {
                return root.get(pathFields.get(0));
            } else {
                return join.get(pathFields.get(0));
            }
        }
    }

    @Override
    public Predicate toPredicate(final Root<T> root, final CriteriaQuery<?> criteriaQuery, final CriteriaBuilder criteriaBuilder) {
        if (this.filterCriteria != null) {
            final List<String> pathFields = Arrays.asList(this.filterCriteria.getInternalFieldName().split("\\."));
            final Path path = SpecificationFilter.fieldPath(root, null, pathFields);

            final Function<String, ? extends Comparable> converter = FilterConverter.getConverter(path.getJavaType())
                    .orElseThrow(() -> new FilterConverterException(
                            String.format("No filter converter found for type %s while trying to convert the field %s",
                                    path.getJavaType().getName(),
                                    this.filterCriteria.getInternalFieldName()
                            )
                    ));

            try {
                if (this.filterCriteria.getValue() != null) {
                    if (this.filterCriteria.getOperator().equals(FilterOperatorEnum.EQUAL)) {
                        return criteriaBuilder.equal(path, converter.apply(this.filterCriteria.getValue()));
                    } else if (this.filterCriteria.getOperator().equals(FilterOperatorEnum.NOT_EQUAL)) {
                        return criteriaBuilder.notEqual(path, converter.apply(this.filterCriteria.getValue()));
                    } else if (this.filterCriteria.getOperator().equals(FilterOperatorEnum.GREATER_THAN)) {
                        return criteriaBuilder.greaterThan(path, converter.apply(this.filterCriteria.getValue()));
                    } else if (this.filterCriteria.getOperator().equals(FilterOperatorEnum.GREATER_THAN_OR_EQUAL)) {
                        return criteriaBuilder.greaterThanOrEqualTo(path, converter.apply(this.filterCriteria.getValue()));
                    } else if (this.filterCriteria.getOperator().equals(FilterOperatorEnum.LESS_THAN)) {
                        return criteriaBuilder.lessThan(path, converter.apply(this.filterCriteria.getValue()));
                    } else if (this.filterCriteria.getOperator().equals(FilterOperatorEnum.LESS_THAN_OR_EQUAL)) {
                        return criteriaBuilder.lessThanOrEqualTo(path, converter.apply(this.filterCriteria.getValue()));
                    } else if (this.filterCriteria.getOperator().equals(FilterOperatorEnum.LIKE)) {
                        return criteriaBuilder.like(path, "%" + converter.apply(this.filterCriteria.getValue() + "%"));
                    } else if (this.filterCriteria.getOperator().equals(FilterOperatorEnum.NULL) && !this.filterCriteria.getValue().equals("false")) {
                        return criteriaBuilder.isNull(path);
                    } else if (this.filterCriteria.getOperator().equals(FilterOperatorEnum.NULL) && this.filterCriteria.getValue().equals("false")) {
                        return criteriaBuilder.isNotNull(path);
                    }
                } else if (this.filterCriteria.getValues() != null) {
                    if (this.filterCriteria.getOperator().equals(FilterOperatorEnum.IN)) {
                        return criteriaBuilder.not(path.in(Arrays.asList(this.filterCriteria.getValues()))).not();
                    } else if (this.filterCriteria.getOperator().equals(FilterOperatorEnum.NOT_IN)) {
                        return criteriaBuilder.not(path.in(Arrays.asList(this.filterCriteria.getValues())));
                    }
                }
            } catch (final DateTimeParseException | IllegalArgumentException e) {
                final InvalidParameterType invalidParameterType =
                        new InvalidParameterType(this.filterCriteria.getExternalFieldName(),
                                this.filterCriteria.getValue(),
                                path.getJavaType().getSimpleName()
                        );
                Logger.getLogger("DataAccess").log(Level.INFO,
                        String.format("%s\nDetails: %s", invalidParameterType.toString(), invalidParameterType.getDetails())
                );
                throw invalidParameterType;
            }
        }

        return null;
    }
}
