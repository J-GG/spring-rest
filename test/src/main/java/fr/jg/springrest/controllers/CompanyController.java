package fr.jg.springrest.controllers;

import fr.jg.springrest.data.pojo.PagedResource;
import fr.jg.springrest.data.pojo.PagedResponse;
import fr.jg.springrest.data.pojo.PartialResource;
import fr.jg.springrest.data.services.PrunableFieldFilter;
import fr.jg.springrest.dto.CompanyDto;
import fr.jg.springrest.exceptions.ResourceNotFoundException;
import fr.jg.springrest.services.CompanyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/company")
public class CompanyController {

    @Autowired
    CompanyService companyService;

    @Autowired
    PrunableFieldFilter prunableFieldFilter;

    @GetMapping("/{id}")
    public CompanyDto getCompany(final PartialResource<CompanyDto> partialResource, @PathVariable final UUID id) {
        final CompanyDto companyDto = this.companyService.getCompany(id)
                .orElseThrow(() -> new ResourceNotFoundException("Company", id));

        return partialResource.prune(companyDto, this.prunableFieldFilter);
    }

    @GetMapping
    public ResponseEntity<List<CompanyDto>> getCompanies(final PagedResource<CompanyDto> pagedResource) {

        final PagedResponse<CompanyDto> pagedResponse = this.companyService.getCompanies(pagedResource);

        final HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setAll(pagedResponse.getHeaders());

        return ResponseEntity
                .ok()
                .headers(httpHeaders)
                .body(pagedResponse.prune(this.prunableFieldFilter));
    }

    @PatchMapping("/{id}")
    public CompanyDto test(@PathVariable final UUID id, @RequestBody final CompanyDto companyDto) {
        return this.companyService.patchCompany(id, companyDto).orElseThrow(() -> new ResourceNotFoundException("Company", id));
    }

}
