package com.example.services;

import com.example.dto.CompanyDto;
import com.example.toolbox.PagedResource;
import com.example.toolbox.PagedResponse;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CompanyService {

    Optional<CompanyDto> getCompany(UUID id);

    List<CompanyDto> getCompanies();

    PagedResponse<CompanyDto> getCompanies(PagedResource<CompanyDto> pagedResource);
}
