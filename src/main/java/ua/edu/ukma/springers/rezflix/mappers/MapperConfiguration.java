package ua.edu.ukma.springers.rezflix.mappers;

import org.mapstruct.MapperConfig;
import org.mapstruct.MappingConstants;
import org.mapstruct.NullValueMappingStrategy;

@MapperConfig(
        componentModel = MappingConstants.ComponentModel.SPRING,
        implementationPackage = "<PACKAGE_NAME>.generated",
        uses = {EnumsMapper.class, DefaultTypesMapper.class},
        nullValueIterableMappingStrategy = NullValueMappingStrategy.RETURN_DEFAULT
)
public class MapperConfiguration {
}
