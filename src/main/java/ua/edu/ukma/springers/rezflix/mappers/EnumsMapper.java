package ua.edu.ukma.springers.rezflix.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import ua.edu.ukma.springers.rezflix.controllers.rest.model.EpisodeStatusDto;
import ua.edu.ukma.springers.rezflix.controllers.rest.model.FileTypeDto;
import ua.edu.ukma.springers.rezflix.controllers.rest.model.UserRoleDto;
import ua.edu.ukma.springers.rezflix.controllers.rest.model.UserTypeDto;
import ua.edu.ukma.springers.rezflix.domain.enums.FileType;
import ua.edu.ukma.springers.rezflix.domain.enums.FilmEpisodeStatus;
import ua.edu.ukma.springers.rezflix.domain.enums.UserRole;
import ua.edu.ukma.springers.rezflix.domain.enums.UserType;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        implementationPackage = "<PACKAGE_NAME>.generated"
)
public interface EnumsMapper {

    UserTypeDto map(UserType type);
    UserType map(UserTypeDto type);

    UserRoleDto map(UserRole role);

    FilmEpisodeStatus map(EpisodeStatusDto status);
    EpisodeStatusDto map(FilmEpisodeStatus status);

    FileType map(FileTypeDto type);
    FileTypeDto map(FileType type);
}