package ua.edu.ukma.springers.rezflix.validators;

import org.springframework.stereotype.Component;
import ua.edu.ukma.springers.rezflix.domain.entities.UserEntity;
import ua.edu.ukma.springers.rezflix.exceptions.ValidationException;
import ua.edu.ukma.springers.rezflix.repositories.UserRepository;

@Component
public class UserValidator extends BaseValidator<UserEntity> {

    private final UserRepository userRepository;

    public UserValidator(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void validForCreate(UserEntity user) {
        validateData(user);
        validateUsernameIsUnique(user);
    }

    private void validateUsernameIsUnique(UserEntity user) {
        if (userRepository.existsByUsername(user.getUsername()))
            throw new ValidationException("error.user.username.duplicate");
    }
}
