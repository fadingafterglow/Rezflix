package ua.edu.ukma.springers.rezflix.validators;

import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ua.edu.ukma.springers.rezflix.domain.entities.FilmCollectionEntity;
import ua.edu.ukma.springers.rezflix.domain.enums.UserRole;
import ua.edu.ukma.springers.rezflix.security.SecurityUtils;
import java.util.Collections;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FilmCollectionValidatorTest {

    @Mock private SecurityUtils securityUtils;
    @Mock private Validator validator;
    @Mock private FilmCollectionEntity collectionEntity;
    private FilmCollectionValidator testingValidator;

    @BeforeEach
    void setUp() {
        testingValidator = new FilmCollectionValidator(securityUtils);
        testingValidator.setValidator(validator);
        when(collectionEntity.getOwnerId()).thenReturn(123);
        when(securityUtils.getCurrentUserId()).thenReturn(456);
    }

    @Test
    @DisplayName("Should allow update if current user is owner")
    void validForUpdate_Owner() {
        when(validator.validate(collectionEntity)).thenReturn(Collections.emptySet());
        when(securityUtils.getCurrentUserId()).thenReturn(123);
        assertDoesNotThrow(() -> testingValidator.validForUpdate(collectionEntity));
        verify(securityUtils, never()).requireRole(any());
    }

    @Test
    @DisplayName("Should check for Moderator role if current user is NOT owner")
    void validForUpdate_NotOwner_ChecksModerator() {
        when(validator.validate(collectionEntity)).thenReturn(Collections.emptySet());
        assertDoesNotThrow(() -> testingValidator.validForUpdate(collectionEntity));
        verify(securityUtils).requireRole(UserRole.MODERATOR);
    }

    @Test
    @DisplayName("Should check permissions on delete")
    void validForDelete_ChecksPermissions() {
        assertDoesNotThrow(() -> testingValidator.validForDelete(collectionEntity));
        verify(securityUtils).requireRole(UserRole.MODERATOR);
    }

    @Test
    @DisplayName("Should check permissions on view")
    void validForView_ChecksPermissions() {
        assertDoesNotThrow(() -> testingValidator.validForView(collectionEntity));
        verify(securityUtils).requireRole(UserRole.MODERATOR);
    }
}