package ua.edu.ukma.springers.rezflix.validators;

import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;
import ua.edu.ukma.springers.rezflix.domain.entities.FileInfoEntity;
import ua.edu.ukma.springers.rezflix.domain.interfaces.IGettableById;
import ua.edu.ukma.springers.rezflix.exceptions.ValidationException;
import ua.edu.ukma.springers.rezflix.repositories.IRepository;
import ua.edu.ukma.springers.rezflix.security.SecurityUtils;

import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

public abstract class BaseFileValidator<E extends IGettableById<Integer>> implements IFileValidator {

    @Setter(onMethod_ =  @Autowired)
    protected SecurityUtils securityUtils;
    @Setter(onMethod_ =  @Autowired)
    protected IRepository<E, Integer> repository;

    protected final Pattern typePattern;

    protected BaseFileValidator(String typePattern) {
        this.typePattern = Pattern.compile(typePattern);
    }

    @Override
    public void validForView(FileInfoEntity fileInfo) {}

    @Override
    public void validForView(List<FileInfoEntity> fileInfos) {
        for (FileInfoEntity fileInfo : fileInfos)
            validForView(fileInfo);
    }

    @Override
    public void validForUpload(MultipartFile file, int boundEntityId) {
        validateFile(file);
        validateEntityExists(boundEntityId);
    }

    @Override
    public void validForDelete(FileInfoEntity fileInfo) {}

    protected final void validateFile(MultipartFile file) {
        if (file == null || file.getSize() == 0)
            throw new ValidationException("error.file.empty");
        if (!typePattern.matcher(Objects.requireNonNullElse(file.getContentType(), StringUtils.EMPTY)).matches())
            throw new ValidationException("error.file.invalid-content-type");
    }

    protected final void validateEntityExists(int entityId) {
        if (!repository.existsById(entityId))
            throw new ValidationException("error.file.bound_entity.not_existent");
    }
}
