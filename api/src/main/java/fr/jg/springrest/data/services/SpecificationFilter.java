package fr.jg.springrest.data.services;

import fr.jg.springrest.data.enumerations.FilterOperatorEnum;
import fr.jg.springrest.data.exceptions.FilterConverterException;
import fr.jg.springrest.data.exceptions.InvalidParameterType;
import fr.jg.springrest.data.pojo.FilterCriterion;
import fr.jg.springrest.data.utility.FilterConverter;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Generic implementation of {@link Specification}.
 *
 * @param <T> The entity on which is specification is applied.
 */
public class SpecificationFilter<T> implements Specification<T> {

    /**
     * The filter criterion to apply.
     */
    private final FilterCriterion filterCriterion;

    /**
     * Constructor.
     *
     * @param filterCriterion The filter criterion to apply.
     */
    public SpecificationFilter(final FilterCriterion filterCriterion) {
        this.filterCriterion = filterCriterion;
    }

    /**
     * Gets the path of the field which is intended to be filtered.
     * <p>
     * Performs joins when necessary.
     *
     * @param root       The root.
     * @param join       The join.
     * @param pathFields The full path as a list of field names.
     * @return The path of the field which is intended to be filtered.
     */
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
        if (this.filterCriterion != null) {
            final List<String> pathFields = Arrays.asList(this.filterCriterion.getInternalFieldName().split("\\."));
            final Path path = SpecificationFilter.fieldPath(root, null, pathFields);

            final Function<String, ? extends Comparable> converter = FilterConverter.getConverter(path.getJavaType())
                    .orElseThrow(() -> new FilterConverterException(
                            String.format("No filter converter found for type %s while trying to convert the field %s",
                                    path.getJavaType().getName(),
                                    this.filterCriterion.getInternalFieldName()
                            )
                    ));

            try {
                if (this.filterCriterion.getValue() != null) {
                    if (this.filterCriterion.getOperator().equals(FilterOperatorEnum.EQUAL)) {
                        return criteriaBuilder.equal(path, converter.apply(this.filterCriterion.getValue()));
                    } else if (this.filterCriterion.getOperator().equals(FilterOperatorEnum.NOT_EQUAL)) {
                        return criteriaBuilder.notEqual(path, converter.apply(this.filterCriterion.getValue()));
                    } else if (this.filterCriterion.getOperator().equals(FilterOperatorEnum.GREATER_THAN)) {
                        return criteriaBuilder.greaterThan(path, converter.apply(this.filterCriterion.getValue()));
                    } else if (this.filterCriterion.getOperator().equals(FilterOperatorEnum.GREATER_THAN_OR_EQUAL)) {
                        return criteriaBuilder.greaterThanOrEqualTo(path, converter.apply(this.filterCriterion.getValue()));
                    } else if (this.filterCriterion.getOperator().equals(FilterOperatorEnum.LESS_THAN)) {
                        return criteriaBuilder.lessThan(path, converter.apply(this.filterCriterion.getValue()));
                    } else if (this.filterCriterion.getOperator().equals(FilterOperatorEnum.LESS_THAN_OR_EQUAL)) {
                        return criteriaBuilder.lessThanOrEqualTo(path, converter.apply(this.filterCriterion.getValue()));
                    } else if (this.filterCriterion.getOperator().equals(FilterOperatorEnum.LIKE)) {
                        return criteriaBuilder.like(path, "%" + converter.apply(this.filterCriterion.getValue() + "%"));
                    } else if (this.filterCriterion.getOperator().equals(FilterOperatorEnum.NULL) && this.filterCriterion.getValue().equals("true")) {
                        return criteriaBuilder.isNull(path);
                    } else if (this.filterCriterion.getOperator().equals(FilterOperatorEnum.NULL) && this.filterCriterion.getValue().equals("false")) {
                        return criteriaBuilder.isNotNull(path);
                    } else if (this.filterCriterion.getOperator().equals(FilterOperatorEnum.BOOL) && this.filterCriterion.getValue().equals("true")) {
                        return criteriaBuilder.isTrue(path);
                    } else if (this.filterCriterion.getOperator().equals(FilterOperatorEnum.BOOL) && this.filterCriterion.getValue().equals("false")) {
                        return criteriaBuilder.isFalse(path);
                    }
                } else if (this.filterCriterion.getValues() != null) {
                    if (this.filterCriterion.getOperator().equals(FilterOperatorEnum.IN)) {
                        return criteriaBuilder.not(path.in(Arrays.asList(this.filterCriterion.getValues()))).not();
                    } else if (this.filterCriterion.getOperator().equals(FilterOperatorEnum.NOT_IN)) {
                        return criteriaBuilder.not(path.in(Arrays.asList(this.filterCriterion.getValues())));
                    }
                }
            } catch (final DateTimeParseException | IllegalArgumentException e) {
                final InvalidParameterType invalidParameterType =
                        new InvalidParameterType("The type of the parameter is incorrect.", this.filterCriterion.getExternalFieldName(),
                                this.filterCriterion.getValue(),
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
