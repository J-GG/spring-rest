package fr.jg.springrest.services;

import fr.jg.springrest.data.pojo.PagedResource;
import fr.jg.springrest.data.pojo.PagedResponse;
import fr.jg.springrest.data.services.FilterableFieldFilter;
import fr.jg.springrest.data.services.SortableFieldFilter;
import fr.jg.springrest.data.services.SpecificationDataAccess;
import fr.jg.springrest.dto.CompanyDto;
import fr.jg.springrest.entities.CompanyEntity;
import fr.jg.springrest.mappers.CompanyMapper;
import fr.jg.springrest.repositories.CompanyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class CompanyServiceImpl implements CompanyService, SpecificationDataAccess<CompanyDto, CompanyEntity, CompanyRepository, CompanyMapper> {

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private SortableFieldFilter sortableFieldFilter;

    @Autowired
    private FilterableFieldFilter filterableFieldFilter;

    @Override
    public Optional<CompanyDto> getCompany(final UUID id) {
        final Optional<CompanyEntity> companyEntity = this.companyRepository.findById(id);
        return companyEntity.map(CompanyMapper.INSTANCE::map);
    }

    @Override
    public PagedResponse<CompanyDto> getCompanies(final PagedResource<CompanyDto> pagedResource) {
        return this.get(pagedResource, this.companyRepository, this.filterableFieldFilter, this.sortableFieldFilter);
    }
}
