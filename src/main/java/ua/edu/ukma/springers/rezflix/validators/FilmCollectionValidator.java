package ua.edu.ukma.springers.rezflix.validators;

import jakarta.validation.Validator;
import org.springframework.stereotype.Component;
import ua.edu.ukma.springers.rezflix.domain.entities.FilmCollectionEntity;
import ua.edu.ukma.springers.rezflix.utils.SecurityUtils;

@Component
public class FilmCollectionValidator extends BaseValidator<FilmCollectionEntity> {
    public FilmCollectionValidator(Validator validator, SecurityUtils securityUtils) {
        super(validator, securityUtils);
    }
}
