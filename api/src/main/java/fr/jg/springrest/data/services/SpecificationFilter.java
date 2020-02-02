package fr.jg.springrest.data.services;

import fr.jg.springrest.data.enumerations.FilterOperatorEnum;
import fr.jg.springrest.data.exceptions.FilterConverterException;
import fr.jg.springrest.data.pojo.FilterCriteria;
import fr.jg.springrest.data.toolbox.FilterConverter;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.Arrays;
import java.util.function.Function;

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
            final Function<String, ? extends Comparable> converter = FilterConverter.getConverter(root.get(this.filterCriteria.getField()).getJavaType())
                    .orElseThrow(() -> new FilterConverterException(
                            String.format("No filter converter found for type %s while trying to convert the field %s",
                                    root.get(this.filterCriteria.getField()).getJavaType().getName(),
                                    this.filterCriteria.getField()
                            )
                    ));


            if (this.filterCriteria.getValue() != null) {
                if (this.filterCriteria.getOperator().equals(FilterOperatorEnum.EQUAL)) {
                    return criteriaBuilder.equal(root.get(this.filterCriteria.getField()), converter.apply(this.filterCriteria.getValue()));
                } else if (this.filterCriteria.getOperator().equals(FilterOperatorEnum.NOT_EQUAL)) {
                    return criteriaBuilder.notEqual(root.get(this.filterCriteria.getField()), converter.apply(this.filterCriteria.getValue()));
                } else if (this.filterCriteria.getOperator().equals(FilterOperatorEnum.GREATER_THAN)) {
                    return criteriaBuilder.greaterThan(root.get(this.filterCriteria.getField()), converter.apply(this.filterCriteria.getValue()));
                } else if (this.filterCriteria.getOperator().equals(FilterOperatorEnum.GREATER_THAN_OR_EQUAL)) {
                    return criteriaBuilder.greaterThanOrEqualTo(root.get(this.filterCriteria.getField()), converter.apply(this.filterCriteria.getValue()));
                } else if (this.filterCriteria.getOperator().equals(FilterOperatorEnum.LESS_THAN)) {
                    return criteriaBuilder.lessThan(root.get(this.filterCriteria.getField()), converter.apply(this.filterCriteria.getValue()));
                } else if (this.filterCriteria.getOperator().equals(FilterOperatorEnum.LESS_THAN_OR_EQUAL)) {
                    return criteriaBuilder.lessThanOrEqualTo(root.get(this.filterCriteria.getField()), converter.apply(this.filterCriteria.getValue()));
                } else if (this.filterCriteria.getOperator().equals(FilterOperatorEnum.LIKE)) {
                    return criteriaBuilder.like(root.get(this.filterCriteria.getField()), "%" + converter.apply(this.filterCriteria.getValue() + "%"));
                } else if (this.filterCriteria.getOperator().equals(FilterOperatorEnum.NULL) && !this.filterCriteria.getValue().equals("false")) {
                    return criteriaBuilder.isNull(root.get(this.filterCriteria.getField()));
                } else if (this.filterCriteria.getOperator().equals(FilterOperatorEnum.NULL) && this.filterCriteria.getValue().equals("false")) {
                    return criteriaBuilder.isNotNull(root.get(this.filterCriteria.getField()));
                }
            } else if (this.filterCriteria.getValues() != null) {
                if (this.filterCriteria.getOperator().equals(FilterOperatorEnum.IN)) {
                    return criteriaBuilder.not(root.get(this.filterCriteria.getField()).in(Arrays.asList(this.filterCriteria.getValues()))).not();
                } else if (this.filterCriteria.getOperator().equals(FilterOperatorEnum.NOT_IN)) {
                    return criteriaBuilder.not(root.get(this.filterCriteria.getField()).in(Arrays.asList(this.filterCriteria.getValues())));
                }
            }
        }

        return null;
    }
}
