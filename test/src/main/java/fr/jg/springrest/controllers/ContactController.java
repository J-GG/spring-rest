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

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/contacts")
public class ContactController {
    @Autowired
    ContactService contactService;

    @Autowired
    Validator validator;

    @GetMapping("/{id}")
    public ContactDto getContact(@PathVariable final UUID id) {
        return this.contactService.getContact(id).orElseThrow(() -> new ResourceNotFoundException("Contact", id));
    }

    @GetMapping
    public PagedResponse<ContactDto> getCompanies(final PagedQuery<ContactDto> pagedQuery) {
        return this.contactService.getContacts(pagedQuery);
    }
}
