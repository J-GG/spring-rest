package fr.jg.springrest.mappers;

import fr.jg.springrest.dto.ContactDto;
import fr.jg.springrest.entities.ContactEntity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ContactMapper {
    ContactMapper INSTANCE = Mappers.getMapper(ContactMapper.class);

    ContactDto map(ContactEntity contactEntity);

    ContactEntity map(ContactDto contactDto);
}
