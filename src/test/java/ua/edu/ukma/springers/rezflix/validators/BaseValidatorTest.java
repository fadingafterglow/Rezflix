package ua.edu.ukma.springers.rezflix.validators;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.Data;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ua.edu.ukma.springers.rezflix.domain.interfaces.IGettableById;
import ua.edu.ukma.springers.rezflix.exceptions.ValidationException;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BaseValidatorTest {
    @Mock
    private Validator validator;
    private BaseValidatorImpl testingValidator;
    private TestEntity testEntity;
    @BeforeEach
    void setUp() {
        testingValidator = new BaseValidatorImpl();
        testingValidator.setValidator(validator);
        testEntity = new TestEntity(1, "Test Data");
    }
    @Test
    @DisplayName("Should pass without validation for single entity view")
    void validForView() {
        assertDoesNotThrow(() -> testingValidator.validForView(testEntity));
        verifyNoInteractions(validator);
    }
    @Test
    @DisplayName("Should pass without validation for entity list view")
    void testValidForView() {
        List<TestEntity> list = List.of(testEntity);
        assertDoesNotThrow(() -> testingValidator.validForView(list));
        verifyNoInteractions(validator);
    }
    @Test
    @DisplayName("Should call validator during creation")
    void validForCreate() {
        when(validator.validate(testEntity)).thenReturn(Collections.emptySet());
        assertDoesNotThrow(() -> testingValidator.validForCreate(testEntity));
        verify(validator, times(1)).validate(testEntity);
    }
    @Test
    @DisplayName("Should call validator during update")
    void validForUpdate() {
        when(validator.validate(testEntity)).thenReturn(Collections.emptySet());
        assertDoesNotThrow(() -> testingValidator.validForUpdate(testEntity));
        verify(validator, times(1)).validate(testEntity);
    }
    @Test
    @DisplayName("Should do nothing during delete")
    void validForDelete() {
        assertDoesNotThrow(() -> testingValidator.validForDelete(testEntity));
        verifyNoInteractions(validator);
    }
    @Test
    @DisplayName("Should throw ValidationException when violations exist")
    @SuppressWarnings("unchecked")
    void validateData() {
        ConstraintViolation<TestEntity> violation = mock(ConstraintViolation.class);
        when(validator.validate(testEntity)).thenReturn(Set.of(violation));
        assertThrows(ValidationException.class, () -> testingValidator.validateData(testEntity));
    }
    @Test
    @DisplayName("Should correctly set validator dependency")
    void setValidator() {
        BaseValidatorImpl localValidator = new BaseValidatorImpl();
        localValidator.setValidator(validator);
        assertNotNull(localValidator.validator);
    }
    private static class BaseValidatorImpl extends BaseValidator<TestEntity> {}

    @Data
    private static class TestEntity implements IGettableById<Integer> {
        private Integer id;
        private String data;
        public TestEntity(Integer id, String data) { this.id = id; this.data = data; }
    }
}