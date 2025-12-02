package ua.edu.ukma.springers.rezflix.validators;

import org.springframework.web.multipart.MultipartFile;
import ua.edu.ukma.springers.rezflix.domain.entities.FileInfoEntity;
import ua.edu.ukma.springers.rezflix.domain.enums.FileType;

import java.util.List;

public interface IFileValidator {

    FileType supportedFileType();

    void validForView(FileInfoEntity fileInfo);

    void validForView(List<FileInfoEntity> fileInfo);

    void validForUpload(MultipartFile file, int boundEntityId);

    void validForDelete(FileInfoEntity fileInfo);
}