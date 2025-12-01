package ua.edu.ukma.springers.rezflix.mergers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import ua.edu.ukma.springers.rezflix.controllers.rest.model.CreateUserDto;
import ua.edu.ukma.springers.rezflix.controllers.rest.model.UpdateUserDto;
import ua.edu.ukma.springers.rezflix.controllers.rest.model.UserTypeDto;
import ua.edu.ukma.springers.rezflix.domain.entities.UserEntity;
import ua.edu.ukma.springers.rezflix.domain.enums.UserType;
import ua.edu.ukma.springers.rezflix.mappers.EnumsMapper;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserMergerTest {

    @Mock private EnumsMapper enumsMapper;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private UserEntity entity;

    private UserMerger merger;
    private CreateUserDto createDto;
    private UpdateUserDto updateDto;

    @BeforeEach
    void setUp() {
        merger = new UserMerger(enumsMapper, passwordEncoder);
        createDto = new CreateUserDto();
        createDto.setUsername("user");
        createDto.setPassword("pass");
        createDto.setAbout("about");
        createDto.setType(UserTypeDto.MODERATOR);
        updateDto = new UpdateUserDto();
        updateDto.setAbout("updated info");
    }

    @Test
    @DisplayName("Should merge all fields including password and role for create")
    void mergeForCreate() {
        when(passwordEncoder.encode("pass")).thenReturn("encoded");
        when(enumsMapper.map(UserTypeDto.MODERATOR)).thenReturn(UserType.MODERATOR);
        merger.mergeForCreate(entity, createDto);
        verify(entity).setUsername("user");
        verify(entity).setPasswordHash("encoded");
        verify(entity).setType(UserType.MODERATOR);
        verify(entity).setAbout("about");
    }

    @Test
    @DisplayName("Should merge only mutable fields for update")
    void mergeForUpdate() {
        merger.mergeForUpdate(entity, updateDto);
        verify(entity).setAbout("updated info");
    }
}