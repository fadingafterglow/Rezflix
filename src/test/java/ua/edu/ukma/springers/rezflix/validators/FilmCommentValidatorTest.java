package ua.edu.ukma.springers.rezflix.validators;

import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ua.edu.ukma.springers.rezflix.domain.entities.FilmCommentEntity;
import ua.edu.ukma.springers.rezflix.domain.enums.UserRole;
import ua.edu.ukma.springers.rezflix.security.SecurityUtils;
import java.util.Collections;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FilmCommentValidatorTest {

    @Mock private SecurityUtils securityUtils;
    @Mock private Validator validator;
    @Mock private FilmCommentEntity commentEntity;
    private FilmCommentValidator testingValidator;

    @BeforeEach
    void setUp() {
        testingValidator = new FilmCommentValidator(securityUtils);
        testingValidator.setValidator(validator);
        when(commentEntity.getAuthorId()).thenReturn(55);
        when(securityUtils.getCurrentUserId()).thenReturn(77);
    }

    @Test
    @DisplayName("Should allow update if current user is author")
    void validForUpdate_Author() {
        when(validator.validate(commentEntity)).thenReturn(Collections.emptySet());
        when(securityUtils.getCurrentUserId()).thenReturn(55);
        assertDoesNotThrow(() -> testingValidator.validForUpdate(commentEntity));
        verify(securityUtils, never()).requireRole(any());
    }

    @Test
    @DisplayName("Should require Moderator role if user is not author during update")
    void validForUpdate_NotAuthor() {
        when(validator.validate(commentEntity)).thenReturn(Collections.emptySet());
        assertDoesNotThrow(() -> testingValidator.validForUpdate(commentEntity));
        verify(securityUtils).requireRole(UserRole.MODERATOR);
    }

    @Test
    @DisplayName("Should require Moderator role if user is not author during delete")
    void validForDelete_NotAuthor() {
        assertDoesNotThrow(() -> testingValidator.validForDelete(commentEntity));
        verify(securityUtils).requireRole(UserRole.MODERATOR);
    }
}