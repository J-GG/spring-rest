package fr.jg.springrest.services;

import fr.jg.springrest.*;
import fr.jg.springrest.dto.CompanyDto;
import fr.jg.springrest.entities.CompanyEntity;
import fr.jg.springrest.enumerations.SortingOrderEnum;
import fr.jg.springrest.functional.FilterableFieldFilter;
import fr.jg.springrest.functional.SortableFieldFilter;
import fr.jg.springrest.mappers.CompanyMapper;
import fr.jg.springrest.repositories.CompanyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class CompanyServiceImpl implements CompanyService {

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private SortableFieldFilter sortableFieldFilter;

    @Autowired
    private FilterableFieldFilter filterableFieldFilter;

    @Autowired
    private SpecificationDataAccess<CompanyDto, CompanyEntity, CompanyRepository, CompanyMapper> dataAccess;

    @Override
    public Optional<CompanyDto> getCompany(final UUID id) {
        final Optional<CompanyEntity> companyEntity = this.companyRepository.findById(id);
        return companyEntity.map(CompanyMapper.INSTANCE::map);
    }

    @Override
    public PagedResponse<CompanyDto> getCompanies(final PagedResource<CompanyDto> pagedResource) {
        final PagedResponse pagedResponse = this.dataAccess.get(pagedResource);

        Specification<CompanyEntity> specification = null;
        for (final FilterCriteria filterCriteria : pagedResource.getFiltersForClass(CompanyDto.class, this.filterableFieldFilter)) {
            final FilterSpecification<CompanyEntity> filterSpecification = new FilterSpecification<>(filterCriteria);
            specification = specification == null ? filterSpecification : specification.and(filterSpecification);
        }

        final List<Sort.Order> orders = pagedResource.getSortForClass(CompanyDto.class, this.sortableFieldFilter)
                .entrySet()
                .stream()
                .filter(entry -> Stream.of(CompanyDto.class.getDeclaredFields()).anyMatch(field -> {
                    field.setAccessible(true);
                    return field.getName().equals(entry.getKey());
                }))
                .map(entry -> {
                    if (entry.getValue().equals(SortingOrderEnum.ASCENDING)) {
                        return Sort.Order.asc(entry.getKey());
                    } else {
                        return Sort.Order.desc(entry.getKey());
                    }
                }).collect(Collectors.toList());

        final PageRequest pageRequest = PageRequest.of(pagedResource.getPage().orElse(0), pagedResource.getPerPage().orElse(10), Sort.by(orders));
        final Page<CompanyEntity> companiesEntity = this.companyRepository.findAll(specification, pageRequest);

        return new PagedResponse<>(pagedResource,
                companiesEntity.getNumberOfElements(),
                companiesEntity.getTotalPages(),
                companiesEntity.getTotalElements(),
                CompanyMapper.INSTANCE.map(companiesEntity.getContent())
        );
    }
}
