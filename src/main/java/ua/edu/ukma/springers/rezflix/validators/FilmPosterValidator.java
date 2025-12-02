package ua.edu.ukma.springers.rezflix.validators;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import ua.edu.ukma.springers.rezflix.domain.entities.FileInfoEntity;
import ua.edu.ukma.springers.rezflix.domain.entities.FilmEntity;
import ua.edu.ukma.springers.rezflix.domain.enums.FileType;
import ua.edu.ukma.springers.rezflix.domain.enums.UserRole;

@Component
public class FilmPosterValidator extends BaseFileValidator<FilmEntity> {

    public FilmPosterValidator() {
        super("image/.{1,127}");
    }

    @Override
    public FileType supportedFileType() {
        return FileType.FILM_POSTER;
    }

    @Override
    public void validForUpload(MultipartFile file, int boundEntityId) {
        super.validForUpload(file, boundEntityId);
        securityUtils.requireRole(UserRole.CONTENT_MANAGER);
    }

    @Override
    public void validForDelete(FileInfoEntity fileInfo) {
        securityUtils.requireRole(UserRole.CONTENT_MANAGER);
    }
}