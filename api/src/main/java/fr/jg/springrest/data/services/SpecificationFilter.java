package fr.jg.springrest.data.services;

import fr.jg.springrest.data.enumerations.FilterOperatorEnum;
import fr.jg.springrest.data.exceptions.FilterConverterException;
import fr.jg.springrest.data.exceptions.InvalidParameterType;
import fr.jg.springrest.data.pojo.FilterCriteria;
import fr.jg.springrest.data.toolbox.FilterConverter;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
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

    @Override
    public Predicate toPredicate(final Root<T> root, final CriteriaQuery<?> criteriaQuery, final CriteriaBuilder criteriaBuilder) {
        if (this.filterCriteria != null) {
            final Function<String, ? extends Comparable> converter = FilterConverter.getConverter(root.get(this.filterCriteria.getInternalFieldName()).getJavaType())
                    .orElseThrow(() -> new FilterConverterException(
                            String.format("No getExternalName converter found for type %s while trying to convert the field %s",
                                    root.get(this.filterCriteria.getInternalFieldName()).getJavaType().getName(),
                                    this.filterCriteria.getInternalFieldName()
                            )
                    ));

            try {
                if (this.filterCriteria.getValue() != null) {
                    if (this.filterCriteria.getOperator().equals(FilterOperatorEnum.EQUAL)) {
                        return criteriaBuilder.equal(root.get(this.filterCriteria.getInternalFieldName()), converter.apply(this.filterCriteria.getValue()));
                    } else if (this.filterCriteria.getOperator().equals(FilterOperatorEnum.NOT_EQUAL)) {
                        return criteriaBuilder.notEqual(root.get(this.filterCriteria.getInternalFieldName()), converter.apply(this.filterCriteria.getValue()));
                    } else if (this.filterCriteria.getOperator().equals(FilterOperatorEnum.GREATER_THAN)) {
                        return criteriaBuilder.greaterThan(root.get(this.filterCriteria.getInternalFieldName()), converter.apply(this.filterCriteria.getValue()));
                    } else if (this.filterCriteria.getOperator().equals(FilterOperatorEnum.GREATER_THAN_OR_EQUAL)) {
                        return criteriaBuilder.greaterThanOrEqualTo(root.get(this.filterCriteria.getInternalFieldName()), converter.apply(this.filterCriteria.getValue()));
                    } else if (this.filterCriteria.getOperator().equals(FilterOperatorEnum.LESS_THAN)) {
                        return criteriaBuilder.lessThan(root.get(this.filterCriteria.getInternalFieldName()), converter.apply(this.filterCriteria.getValue()));
                    } else if (this.filterCriteria.getOperator().equals(FilterOperatorEnum.LESS_THAN_OR_EQUAL)) {
                        return criteriaBuilder.lessThanOrEqualTo(root.get(this.filterCriteria.getInternalFieldName()), converter.apply(this.filterCriteria.getValue()));
                    } else if (this.filterCriteria.getOperator().equals(FilterOperatorEnum.LIKE)) {
                        return criteriaBuilder.like(root.get(this.filterCriteria.getInternalFieldName()), "%" + converter.apply(this.filterCriteria.getValue() + "%"));
                    } else if (this.filterCriteria.getOperator().equals(FilterOperatorEnum.NULL) && !this.filterCriteria.getValue().equals("false")) {
                        return criteriaBuilder.isNull(root.get(this.filterCriteria.getInternalFieldName()));
                    } else if (this.filterCriteria.getOperator().equals(FilterOperatorEnum.NULL) && this.filterCriteria.getValue().equals("false")) {
                        return criteriaBuilder.isNotNull(root.get(this.filterCriteria.getInternalFieldName()));
                    }
                } else if (this.filterCriteria.getValues() != null) {
                    if (this.filterCriteria.getOperator().equals(FilterOperatorEnum.IN)) {
                        return criteriaBuilder.not(root.get(this.filterCriteria.getInternalFieldName()).in(Arrays.asList(this.filterCriteria.getValues()))).not();
                    } else if (this.filterCriteria.getOperator().equals(FilterOperatorEnum.NOT_IN)) {
                        return criteriaBuilder.not(root.get(this.filterCriteria.getInternalFieldName()).in(Arrays.asList(this.filterCriteria.getValues())));
                    }
                }
            } catch (final DateTimeParseException | IllegalArgumentException e) {
                final InvalidParameterType invalidParameterType =
                        new InvalidParameterType(this.filterCriteria.getExternalFieldName(),
                                this.filterCriteria.getValue(),
                                root.get(this.filterCriteria.getInternalFieldName()).getJavaType().getSimpleName()
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
