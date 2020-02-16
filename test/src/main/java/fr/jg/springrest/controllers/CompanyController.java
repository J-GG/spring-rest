package fr.jg.springrest.controllers;

import fr.jg.springrest.data.pojo.PagedQuery;
import fr.jg.springrest.data.pojo.PagedResponse;
import fr.jg.springrest.dto.CompanyDto;
import fr.jg.springrest.exceptions.ResourceNotFoundException;
import fr.jg.springrest.services.CompanyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/company")
public class CompanyController {
    @Autowired
    CompanyService companyService;

    @Autowired
    Validator validator;

    @GetMapping("/{id}")
    public CompanyDto getCompany(@PathVariable final UUID id) {
        return this.companyService.getCompany(id).orElseThrow(() -> new ResourceNotFoundException("Company", id));
    }

    @GetMapping
    public PagedResponse<CompanyDto> getCompanies(final PagedQuery<CompanyDto> pagedQuery) {
        return this.companyService.getCompanies(pagedQuery);
    }

    @PatchMapping("/{id}")
    public CompanyDto patchCompany(@PathVariable final UUID id, @RequestBody final CompanyDto companyDto) {
        final Set<ConstraintViolation<Object>> constraintViolationSet = this.validator.validate(companyDto);
//        if (!constraintViolationSet.isEmpty()) {
//            throw new InvalidResourceException("Company", constraintViolationSet);
//        }

        return this.companyService.patchCompany(id, companyDto).orElseThrow(() -> new ResourceNotFoundException("Company", id));
    }
}
