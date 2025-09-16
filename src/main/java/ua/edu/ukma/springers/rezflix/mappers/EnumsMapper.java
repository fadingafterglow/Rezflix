package ua.edu.ukma.springers.rezflix.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        implementationPackage = "<PACKAGE_NAME>.generated"
)
public interface EnumsMapper {
}