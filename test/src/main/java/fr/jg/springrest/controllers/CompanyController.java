package fr.jg.springrest.controllers;

import fr.jg.springrest.data.enumerations.FilterOperatorEnum;
import fr.jg.springrest.data.enumerations.SortingOrderEnum;
import fr.jg.springrest.data.exceptions.FilterConverterException;
import fr.jg.springrest.data.exceptions.InvalidParameterType;
import fr.jg.springrest.data.pojo.PagedQuery;
import fr.jg.springrest.data.pojo.PagedResponse;
import fr.jg.springrest.data.services.SpecificationFilter;
import fr.jg.springrest.data.utility.FilterConverter;
import fr.jg.springrest.dto.CompanyDto;
import fr.jg.springrest.dto.ContactDto;
import fr.jg.springrest.dto.WrappedPagedResource;
import fr.jg.springrest.entities.CompanyEntity;
import fr.jg.springrest.exceptions.ResourceNotFoundException;
import fr.jg.springrest.mappers.CompanyMapper;
import fr.jg.springrest.services.CompanyService;
import fr.jg.springrest.services.ContactService;
import org.hibernate.query.criteria.internal.compile.CriteriaQueryTypeQueryAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Validator;
import java.lang.reflect.Field;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/companies")
public class CompanyController {
    @PersistenceContext
    private EntityManager entityManager;


    @Autowired
    CompanyService companyService;

    @Autowired
    ContactService contactService;

    @Autowired
    Validator validator;

    @GetMapping("/{id}")
    public CompanyDto getCompany(@PathVariable final UUID id) {
        return this.companyService.getCompany(id).orElseThrow(() -> new ResourceNotFoundException("Company", id));
    }

    @GetMapping
    public PagedResponse<CompanyDto> getCompanies(final PagedQuery<CompanyDto> pagedQuery) {
        final CriteriaBuilder criteriaBuilder = this.entityManager.getCriteriaBuilder();
        final CriteriaQuery<Object[]> query = criteriaBuilder.createQuery(Object[].class);
        final Root<CompanyEntity> company = query.from(CompanyEntity.class);
        final List<WrappedPagedResource<CompanyEntity>> wrappedPagedResources = new ArrayList<>();

        final List<Order> orders = pagedQuery.getSort()
                .entrySet()
                .stream()
                .map(entry -> SortingOrderEnum.ASCENDING.equals(entry.getValue()) ? criteriaBuilder.asc(company.get(entry.getKey())) : criteriaBuilder.desc(company.get(entry.getKey())))
                .collect(Collectors.toList());
        query.orderBy(orders);


        final List<Predicate> predicates = new ArrayList<>();
        pagedQuery.getFilters().forEach(filterCriterion -> {
            final List<String> pathFields = Arrays.asList(filterCriterion.getExternalFieldName().split("\\."));
            final Path path = SpecificationFilter.fieldPath(company, null, pathFields);
            final Function<String, ? extends Comparable> converter = FilterConverter.getConverter(path.getJavaType())
                    .orElseThrow(() -> new FilterConverterException(
                            String.format("No filter converter found for type %s while trying to convert the field %s",
                                    path.getJavaType().getName(),
                                    filterCriterion.getExternalFieldName()
                            )
                    ));

            try {
                Predicate predicate = null;
                if (filterCriterion.getValue() != null) {
                    if (filterCriterion.getOperator().equals(FilterOperatorEnum.EQUAL)) {
                        predicate = criteriaBuilder.equal(path, converter.apply(filterCriterion.getValue()));
                    } else if (filterCriterion.getOperator().equals(FilterOperatorEnum.NOT_EQUAL)) {
                        predicate = criteriaBuilder.notEqual(path, converter.apply(filterCriterion.getValue()));
                    } else if (filterCriterion.getOperator().equals(FilterOperatorEnum.GREATER_THAN)) {
                        predicate = criteriaBuilder.greaterThan(path, converter.apply(filterCriterion.getValue()));
                    } else if (filterCriterion.getOperator().equals(FilterOperatorEnum.GREATER_THAN_OR_EQUAL)) {
                        predicate = criteriaBuilder.greaterThanOrEqualTo(path, converter.apply(filterCriterion.getValue()));
                    } else if (filterCriterion.getOperator().equals(FilterOperatorEnum.LESS_THAN)) {
                        predicate = criteriaBuilder.lessThan(path, converter.apply(filterCriterion.getValue()));
                    } else if (filterCriterion.getOperator().equals(FilterOperatorEnum.LESS_THAN_OR_EQUAL)) {
                        predicate = criteriaBuilder.lessThanOrEqualTo(path, converter.apply(filterCriterion.getValue()));
                    } else if (filterCriterion.getOperator().equals(FilterOperatorEnum.LIKE)) {
                        predicate = criteriaBuilder.like(path, "%" + converter.apply(filterCriterion.getValue() + "%"));
                    } else if (filterCriterion.getOperator().equals(FilterOperatorEnum.NULL) && filterCriterion.getValue().equals("true")) {
                        predicate = criteriaBuilder.isNull(path);
                    } else if (filterCriterion.getOperator().equals(FilterOperatorEnum.NULL) && filterCriterion.getValue().equals("false")) {
                        predicate = criteriaBuilder.isNotNull(path);
                    } else if (filterCriterion.getOperator().equals(FilterOperatorEnum.BOOL) && filterCriterion.getValue().equals("true")) {
                        predicate = criteriaBuilder.isTrue(path);
                    } else if (filterCriterion.getOperator().equals(FilterOperatorEnum.BOOL) && filterCriterion.getValue().equals("false")) {
                        predicate = criteriaBuilder.isFalse(path);
                    } else if (filterCriterion.getOperator().equals(FilterOperatorEnum.EMPTY) && filterCriterion.getValue().equals("true")) {
                        predicate = criteriaBuilder.isEmpty(path);
                    } else if (filterCriterion.getOperator().equals(FilterOperatorEnum.EMPTY) && filterCriterion.getValue().equals("false")) {
                        predicate = criteriaBuilder.isNotEmpty(path);
                    }
                } else if (filterCriterion.getValues() != null) {
                    if (filterCriterion.getOperator().equals(FilterOperatorEnum.IN)) {
                        predicate = criteriaBuilder.not(path.in(Arrays.asList(filterCriterion.getValues()))).not();
                    } else if (filterCriterion.getOperator().equals(FilterOperatorEnum.NOT_IN)) {
                        predicate = criteriaBuilder.not(path.in(Arrays.asList(filterCriterion.getValues())));
                    }
                }
                if (predicate != null) {
                    predicates.add(predicate);
                }
            } catch (final DateTimeParseException | IllegalArgumentException e) {
                final InvalidParameterType invalidParameterType =
                        new InvalidParameterType("The type of the parameter is incorrect.", filterCriterion.getExternalFieldName(),
                                filterCriterion.getValue(),
                                path.getJavaType().getSimpleName()
                        );
                Logger.getLogger("DataAccess").log(Level.INFO,
                        String.format("%s\nDetails: %s", invalidParameterType.toString(), invalidParameterType.getDetails())
                );
                throw invalidParameterType;
            }


        });
        query.where(predicates.toArray(new Predicate[0]));

        final List<Expression<?>> groupBy = new ArrayList<>();
        if (pagedQuery.getGroupBy() != null) {
            groupBy.addAll(pagedQuery.getGroupBy().stream().map(company::get).collect(Collectors.toList()));

            query.groupBy(groupBy);
            final List<Selection<?>> selections = groupBy.stream().map(expression -> (Selection<?>) expression).collect(Collectors.toList());
            selections.add(criteriaBuilder.count(company));
            if (pagedQuery.getSum() != null) {
                pagedQuery.getSum().forEach(s -> selections.add(criteriaBuilder.sum(company.get(s))));
            }
            if (pagedQuery.getMin() != null) {
                pagedQuery.getMin().forEach(s -> selections.add(criteriaBuilder.min(company.get(s))));
            }
            if (pagedQuery.getMax() != null) {
                pagedQuery.getMax().forEach(s -> selections.add(criteriaBuilder.max(company.get(s))));
            }
            if (pagedQuery.getAvg() != null) {
                pagedQuery.getAvg().forEach(s -> selections.add(criteriaBuilder.avg(company.get(s))));
            }
            query.multiselect(selections);
        } else {
            query.multiselect(company);
        }

        final int firstResult = (int) ((pagedQuery.getPage().orElse(1L) - 1) * pagedQuery.getPerPage().orElse(20L));
        final TypedQuery<Object[]> typedQuery = this.entityManager.createQuery(query)
                .setFirstResult(firstResult > 0 ? firstResult : 0)
                .setMaxResults(Math.toIntExact(pagedQuery.getPerPage().orElse(20L)));

        final List<Object[]> resultList = typedQuery.getResultList();
        if (((CriteriaQueryTypeQueryAdapter<Object[]>) typedQuery).getReturnTypes()[0].getReturnedClass().equals(CompanyEntity.class)) {
            for (final Object object : resultList.toArray()) {
                final CompanyEntity companyEntity = (CompanyEntity) object;
                final WrappedPagedResource<CompanyEntity> wrappedPagedResource = new WrappedPagedResource<>();
                wrappedPagedResource.setResource(companyEntity);
                wrappedPagedResources.add(wrappedPagedResource);
            }
        }

        //Order by count
//        query.orderBy(criteriaBuilder.desc(criteriaBuilder.count(company)));

        if (pagedQuery.getGroupBy() != null) {
            int returnedValues = groupBy.size() + 1;
            if (pagedQuery.getSum() != null) {
                returnedValues += pagedQuery.getSum().size();
            }
            if (pagedQuery.getMin() != null) {
                returnedValues += pagedQuery.getMin().size();
            }
            if (pagedQuery.getMax() != null) {
                returnedValues += pagedQuery.getMax().size();
            }
            if (pagedQuery.getAvg() != null) {
                returnedValues += pagedQuery.getAvg().size();
            }

            final int groupByNb = pagedQuery.getGroupBy() == null ? 0 : pagedQuery.getGroupBy().size();
            final int sumNb = pagedQuery.getSum() == null ? 0 : pagedQuery.getSum().size();
            final int minNb = pagedQuery.getMin() == null ? 0 : pagedQuery.getMin().size();
            final int maxNb = pagedQuery.getMax() == null ? 0 : pagedQuery.getMax().size();
            final int avgNb = pagedQuery.getAvg() == null ? 0 : pagedQuery.getAvg().size();
            final int finalReturnedValues = returnedValues;
            resultList.forEach(objects -> {
                final WrappedPagedResource<CompanyEntity> wrappedPagedResource = new WrappedPagedResource<>();
                final CompanyEntity companyEntity = new CompanyEntity();
                for (int i = 0; i < finalReturnedValues; i++) {
                    try {
                        if (i < groupByNb) {
                            final Field f = companyEntity.getClass().getDeclaredField(pagedQuery.getGroupBy().get(i));
                            f.setAccessible(true);
                            f.set(companyEntity, objects[i]);
                        } else if (i < groupByNb + 1) {
                            wrappedPagedResource.getAggregates().put("count", objects[i]);
                        } else if (pagedQuery.getSum() != null && !pagedQuery.getSum().isEmpty() && i < groupByNb + 1 + sumNb) {
                            wrappedPagedResource.getAggregates().put("sum_" + pagedQuery.getSum().get(i - (groupByNb + 1)), objects[i]);
                        } else if (pagedQuery.getMin() != null && !pagedQuery.getMin().isEmpty() && i < groupByNb + 1 + sumNb + minNb) {
                            wrappedPagedResource.getAggregates().put("min_" + pagedQuery.getMin().get(i - (groupByNb + 1 + sumNb)), objects[i]);
                        } else if (pagedQuery.getMax() != null && !pagedQuery.getMax().isEmpty() && i < groupByNb + 1 + sumNb + minNb + maxNb) {
                            wrappedPagedResource.getAggregates().put("max_" + pagedQuery.getMax().get(i - (groupByNb + 1 + sumNb + minNb)), objects[i]);
                        } else if (pagedQuery.getAvg() != null && !pagedQuery.getAvg().isEmpty() && i < groupByNb + 1 + sumNb + minNb + maxNb + avgNb) {
                            wrappedPagedResource.getAggregates().put("avg_" + pagedQuery.getAvg().get(i - (groupByNb + 1 + sumNb + minNb + maxNb)), objects[i]);
                        }
                    } catch (final NoSuchFieldException | IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
                wrappedPagedResource.setResource(companyEntity);
                wrappedPagedResources.add(wrappedPagedResource);
            });
        }
        this.entityManager.close();

        final long size = (long) this.entityManager.createQuery(query).getResultList().size();
        final PagedResponse pagedResponse = new PagedResponse<>(pagedQuery.getSort(), pagedQuery.getFilters(), pagedQuery.getPage().orElse(1L), pagedQuery.getPerPage().orElse(20L), (long) wrappedPagedResources.size(), (long) Math.ceil((double) size / pagedQuery.getPerPage().orElse(20L)), size, wrappedPagedResources.stream().map(wrappedPagedResource -> new WrappedPagedResource<>(CompanyMapper.INSTANCE.map(wrappedPagedResource.getResource()), wrappedPagedResource.getAggregates())).collect(Collectors.toList()));
        return pagedResponse;
        //        return this.companyService.getCompanies(pagedQuery);
    }

    @GetMapping("/{id}/contacts")
    public PagedResponse<ContactDto> getContacts(@PathVariable final UUID id, final PagedQuery<ContactDto> pagedQuery) {
        return this.contactService.getContactsByCompany(pagedQuery, id);
    }

    @PatchMapping("/{id}")
    public CompanyDto patchCompany(@PathVariable final UUID id, @RequestBody final CompanyDto companyDto, final HttpServletRequest request) {
//        final Set<ConstraintViolation<Object>> constraintViolationSet = this.validator.validate(companyDto);
//        if (!constraintViolationSet.isEmpty()) {
//            throw new InvalidResourceException("Company", constraintViolationSet);
//        }
        return this.companyService.patchCompany(id, companyDto, request.getAttribute("PATCH")).orElseThrow(() -> new ResourceNotFoundException("Company", id));
    }
}
