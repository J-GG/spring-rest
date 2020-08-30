package fr.jg.springrest.services;

import fr.jg.springrest.data.pojo.PagedQuery;
import fr.jg.springrest.data.pojo.PagedResponse;
import fr.jg.springrest.dto.ContactDto;

import java.util.Optional;
import java.util.UUID;

public interface ContactService {

    Optional<ContactDto> getContact(UUID id);

    PagedResponse<ContactDto> getContacts(PagedQuery<ContactDto> pagedQuery);

    PagedResponse<ContactDto> getContactsByCompany(PagedQuery<ContactDto> pagedQuery, UUID companyId);

    ContactDto postContact(ContactDto contactDto);

    ContactDto putContact(UUID id, ContactDto contactDto);
}
