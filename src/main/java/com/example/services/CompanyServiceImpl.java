package com.example.services;

import com.example.dto.CompanyDto;
import com.example.entities.CompanyEntity;
import com.example.mappers.CompanyMapper;
import com.example.repositories.CompanyRepository;
import com.example.toolbox.*;
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


    @Override
    public Optional<CompanyDto> getCompany(final UUID id) {
        final Optional<CompanyEntity> companyEntity = this.companyRepository.findById(id);
        return companyEntity.map(CompanyMapper.INSTANCE::map);
    }

    @Override
    public List<CompanyDto> getCompanies() {
        final List<CompanyEntity> companiesEntity = this.companyRepository.findAll();
        return CompanyMapper.INSTANCE.map(companiesEntity);
    }

    @Override
    public PagedResponse<CompanyDto> getCompanies(final PagedResource<CompanyDto> pagedResource) {
        Specification<CompanyEntity> specification = null;
        for (final FilterCriteria filterCriteria : pagedResource.getFiltersForClass(CompanyDto.class)) {
            final FilterSpecification<CompanyEntity> filterSpecification = new FilterSpecification<>(filterCriteria);
            specification = specification == null ? filterSpecification : specification.and(filterSpecification);
        }

        final List<Sort.Order> orders = pagedResource.getSortForClass(CompanyDto.class)
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
