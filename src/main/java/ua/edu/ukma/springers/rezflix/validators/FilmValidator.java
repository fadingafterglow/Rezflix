package ua.edu.ukma.springers.rezflix.validators;

import jakarta.validation.Validator;
import org.springframework.stereotype.Component;
import ua.edu.ukma.springers.rezflix.domain.entities.FilmEntity;
import ua.edu.ukma.springers.rezflix.utils.SecurityUtils;
@Component
public class FilmValidator extends BaseValidator<FilmEntity>{
    public FilmValidator(Validator validator, SecurityUtils securityUtils) {
        super(validator, securityUtils);
    }
}
