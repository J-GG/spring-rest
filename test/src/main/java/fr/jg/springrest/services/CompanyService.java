package fr.jg.springrest.services;

import fr.jg.springrest.PagedResource;
import fr.jg.springrest.PagedResponse;
import fr.jg.springrest.dto.CompanyDto;

import java.util.Optional;
import java.util.UUID;

public interface CompanyService {

    Optional<CompanyDto> getCompany(UUID id);

    PagedResponse<CompanyDto> getCompanies(PagedResource<CompanyDto> pagedResource);
}
