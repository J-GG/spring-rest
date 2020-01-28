package com.example.mappers;

import com.example.dto.CompanyDto;
import com.example.entities.CompanyEntity;
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
