package fr.jg.springrest.mappers;

import fr.jg.springrest.dto.CompanyDto;
import fr.jg.springrest.entities.CompanyEntity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface CompanyMapper {
    CompanyMapper INSTANCE = Mappers.getMapper(CompanyMapper.class);

    CompanyDto map(CompanyEntity companyEntity);

    List<CompanyDto> map(List<CompanyEntity> companyEntity);

    CompanyEntity map(CompanyDto companyDto);
}
