package fr.jg.springrest.services;

import fr.jg.springrest.data.pojo.PagedQuery;
import fr.jg.springrest.data.pojo.PagedResponse;
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
public class CompanyServiceImpl extends SpecificationDataAccess<CompanyDto, CompanyEntity, CompanyRepository, CompanyMapper> implements CompanyService {

    @Autowired
    private CompanyRepository companyRepository;

    @Override
    public Optional<CompanyDto> getCompany(final UUID id) {
        final Optional<CompanyEntity> companyEntity = this.companyRepository.findById(id);
        return companyEntity.map(CompanyMapper.INSTANCE::map);
    }

    @Override
    public PagedResponse<CompanyDto> getCompanies(final PagedQuery<CompanyDto> pagedQuery) {
        return this.get(pagedQuery);
    }

    @Override
    public Optional<CompanyDto> patchCompany(final UUID id, final CompanyDto companyDto, final Object patch) {
        return this.patch(id, companyDto, patch);
    }
}
