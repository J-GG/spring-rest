package fr.jg.springrest.controllers;

import fr.jg.springrest.data.pojo.PagedQuery;
import fr.jg.springrest.data.pojo.PagedResponse;
import fr.jg.springrest.dto.CompanyDto;
import fr.jg.springrest.dto.ContactDto;
import fr.jg.springrest.exceptions.ResourceNotFoundException;
import fr.jg.springrest.services.CompanyService;
import fr.jg.springrest.services.ContactService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/companies")
public class CompanyController {
    @Autowired
    CompanyService companyService;

    @Autowired
    ContactService contactService;

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

    @GetMapping("/{id}/contacts")
    public PagedResponse<ContactDto> getContacts(@PathVariable final UUID id, final PagedQuery<ContactDto> pagedQuery) {
        return this.contactService.getContactsByCompany(pagedQuery, id);
    }

    @PatchMapping("/{id}")
    public CompanyDto patchCompany(@PathVariable final UUID id, @RequestBody final CompanyDto companyDto, final HttpServletRequest request) {
        final Set<ConstraintViolation<Object>> constraintViolationSet = this.validator.validate(companyDto);
//        if (!constraintViolationSet.isEmpty()) {
//            throw new InvalidResourceException("Company", constraintViolationSet);
//        }

        return this.companyService.patchCompany(id, companyDto).orElseThrow(() -> new ResourceNotFoundException("Company", id));
    }
}
