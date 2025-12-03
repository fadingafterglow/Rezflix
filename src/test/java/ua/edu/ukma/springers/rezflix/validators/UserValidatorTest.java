package ua.edu.ukma.springers.rezflix.validators;

import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ua.edu.ukma.springers.rezflix.domain.entities.UserEntity;
import ua.edu.ukma.springers.rezflix.exceptions.ValidationException;
import ua.edu.ukma.springers.rezflix.repositories.UserRepository;
import java.util.Collections;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserValidatorTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private Validator validator;
    @Mock
    private UserEntity userEntity;

    private UserValidator testingValidator;

    @BeforeEach
    void setUp() {
        testingValidator = new UserValidator(userRepository);
        testingValidator.setValidator(validator);
    }

    @Test
    @DisplayName("Should create user successfully if username is unique")
    void validForCreate_UniqueUsername() {
        when(validator.validate(userEntity)).thenReturn(Collections.emptySet());
        when(userEntity.getUsername()).thenReturn("newUser");
        when(userRepository.existsByUsername("newUser")).thenReturn(false);
        assertDoesNotThrow(() -> testingValidator.validForCreate(userEntity));
    }

    @Test
    @DisplayName("Should throw exception if username is duplicate")
    void validForCreate_DuplicateUsername() {
        when(validator.validate(userEntity)).thenReturn(Collections.emptySet());
        when(userEntity.getUsername()).thenReturn("existingUser");
        when(userRepository.existsByUsername("existingUser")).thenReturn(true);
        assertThrows(ValidationException.class, () -> testingValidator.validForCreate(userEntity));
    }
}