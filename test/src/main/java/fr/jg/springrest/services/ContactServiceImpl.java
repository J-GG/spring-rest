package fr.jg.springrest.services;

import fr.jg.springrest.data.enumerations.FilterOperatorEnum;
import fr.jg.springrest.data.pojo.FilterCriterion;
import fr.jg.springrest.data.pojo.PagedQuery;
import fr.jg.springrest.data.pojo.PagedResponse;
import fr.jg.springrest.data.services.SpecificationDataAccess;
import fr.jg.springrest.dto.CompanyDto;
import fr.jg.springrest.dto.ContactDto;
import fr.jg.springrest.entities.CompanyEntity;
import fr.jg.springrest.entities.ContactEntity;
import fr.jg.springrest.mappers.CompanyMapper;
import fr.jg.springrest.mappers.ContactMapper;
import fr.jg.springrest.repositories.CompanyRepository;
import fr.jg.springrest.repositories.ContactRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ContactServiceImpl extends SpecificationDataAccess<ContactDto, ContactEntity, ContactRepository, ContactMapper> implements ContactService {

    @Autowired
    private ContactRepository contactRepository;

    @Override
    public Optional<ContactDto> getContact(final UUID id) {
        final Optional<ContactEntity> contactEntity = this.contactRepository.findById(id);
        return contactEntity.map(ContactMapper.INSTANCE::map);
    }

    @Override
    public PagedResponse<ContactDto> getContacts(final PagedQuery<ContactDto> pagedQuery) {
        return this.get(pagedQuery);
    }

    @Override
    public PagedResponse<ContactDto> getContactsByCompany(PagedQuery<ContactDto> pagedQuery, UUID companyId) {
        final List<FilterCriterion> filterCriteria = new ArrayList<>();
        filterCriteria.add(new FilterCriterion("companies.id", FilterOperatorEnum.EQUAL, companyId.toString()));
        pagedQuery.addFilters(filterCriteria);
        return this.get(pagedQuery);
    }
}
