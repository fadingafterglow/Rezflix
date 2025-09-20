package ua.edu.ukma.springers.rezflix.validators;

import jakarta.validation.Validator;
import org.springframework.stereotype.Component;
import ua.edu.ukma.springers.rezflix.domain.entities.UserEntity;
import ua.edu.ukma.springers.rezflix.domain.enums.UserRole;
import ua.edu.ukma.springers.rezflix.domain.enums.UserType;
import ua.edu.ukma.springers.rezflix.exceptions.ValidationException;
import ua.edu.ukma.springers.rezflix.repositories.UserRepository;
import ua.edu.ukma.springers.rezflix.utils.SecurityUtils;

@Component
public class UserValidator extends BaseValidator<UserEntity> {

    private final UserRepository userRepository;

    public UserValidator(Validator validator, SecurityUtils securityUtils, UserRepository userRepository) {
        super(validator, securityUtils);
        this.userRepository = userRepository;
    }

    @Override
    public void validForCreate(UserEntity user) {
        validateData(user);
        validateUsernameIsUnique(user);
        validateHasPermissionToCreateType(user);
    }

    private void validateUsernameIsUnique(UserEntity user) {
        if (userRepository.existsByUsername(user.getUsername()))
            throw new ValidationException("error.user.username.duplicate");
    }

    private void validateHasPermissionToCreateType(UserEntity user) {
        if (user.getType() != UserType.VIEWER)
            securityUtils.requireRole(UserRole.SUPER_ADMIN);
    }
}
