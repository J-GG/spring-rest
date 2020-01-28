package com.example.controllers;

import com.example.dto.CompanyDto;
import com.example.exceptions.CompanyNotFoundException;
import com.example.services.CompanyService;
import com.example.toolbox.PagedResource;
import com.example.toolbox.PagedResponse;
import com.example.toolbox.PartialResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/company")
public class CompanyController {

    @Autowired
    CompanyService companyService;

    @GetMapping("/{id}")
    public CompanyDto getCompany(final PartialResource<CompanyDto> partialResource, @PathVariable final UUID id) {
        final CompanyDto companyDto = this.companyService.getCompany(id)
                .orElseThrow(() -> new CompanyNotFoundException(String.format("Company %s can't be found. Make sure this id is correct.", id)));
        return partialResource.prune(companyDto);
    }

    @GetMapping
    public ResponseEntity<List<CompanyDto>> getCompanies(final PagedResource<CompanyDto> pagedRequest) {

        final PagedResponse<CompanyDto> pagedResponse = this.companyService.getCompanies(pagedRequest);

        final HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setAll(pagedResponse.getHeaders());

        return ResponseEntity
                .ok()
                .headers(httpHeaders)
                .body(pagedRequest.prune(pagedResponse.getResources()));
    }
}
