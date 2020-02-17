package fr.jg.springrest.mappers;

import fr.jg.springrest.dto.CompanyDto;
import fr.jg.springrest.entities.CompanyEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(uses = {ContactMapper.class})
public interface CompanyMapper {
    CompanyMapper INSTANCE = Mappers.getMapper(CompanyMapper.class);

    @Mapping(source = "contact.id", target = "contactId")
    CompanyDto map(CompanyEntity companyEntity);

    List<CompanyDto> map(List<CompanyEntity> companyEntity);

    @Mapping(source = "contactId", target = "contact.id")
    CompanyEntity map(CompanyDto companyDto);
}
