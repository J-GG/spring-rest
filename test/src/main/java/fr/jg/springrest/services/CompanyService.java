package fr.jg.springrest.services;

import fr.jg.springrest.data.pojo.PagedQuery;
import fr.jg.springrest.data.pojo.PagedResponse;
import fr.jg.springrest.dto.CompanyDto;

import java.util.Optional;
import java.util.UUID;

public interface CompanyService {

    Optional<CompanyDto> getCompany(UUID id);

    PagedResponse<CompanyDto> getCompanies(PagedQuery<CompanyDto> pagedQuery);

    Optional<CompanyDto> patchCompany(UUID id, CompanyDto companyDto);
}
