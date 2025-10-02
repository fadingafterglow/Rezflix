package ua.edu.ukma.springers.rezflix.validators;

import jakarta.validation.Validator;
import org.springframework.stereotype.Component;
import ua.edu.ukma.springers.rezflix.domain.entities.FilmRatingEntity;
import ua.edu.ukma.springers.rezflix.utils.SecurityUtils;
@Component
public class FilmRatingValidator extends BaseValidator<FilmRatingEntity>{

    public FilmRatingValidator(Validator validator, SecurityUtils securityUtils) {
        super(validator, securityUtils);
    }
}
