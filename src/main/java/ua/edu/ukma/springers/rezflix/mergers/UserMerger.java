package ua.edu.ukma.springers.rezflix.mergers;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import ua.edu.ukma.springers.rezflix.controllers.rest.model.CreateUserDto;
import ua.edu.ukma.springers.rezflix.controllers.rest.model.UpdateUserDto;
import ua.edu.ukma.springers.rezflix.domain.entities.UserEntity;
import ua.edu.ukma.springers.rezflix.mappers.EnumsMapper;

@Component
@RequiredArgsConstructor
public class UserMerger implements IMerger<UserEntity, CreateUserDto, UpdateUserDto> {

    private final EnumsMapper enumsMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void mergeForCreate(UserEntity entity, CreateUserDto view) {
        entity.setType(enumsMapper.map(view.getType()));
        entity.setUsername(view.getUsername());
        entity.setPasswordHash(passwordEncoder.encode(view.getPassword()));
        merge(entity, view);
    }

    @Override
    public void mergeForUpdate(UserEntity entity, UpdateUserDto view) {
        merge(entity, view);
    }

    private void merge(UserEntity entity, UpdateUserDto view) {
        entity.setAbout(view.getAbout());
    }
}
