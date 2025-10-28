package ua.edu.ukma.springers.rezflix.validators;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ua.edu.ukma.springers.rezflix.domain.entities.FilmCollectionEntity;
import ua.edu.ukma.springers.rezflix.domain.enums.UserRole;
import ua.edu.ukma.springers.rezflix.security.SecurityUtils;

import java.util.Objects;

@Component
@RequiredArgsConstructor
public class FilmCollectionValidator extends BaseValidator<FilmCollectionEntity> {

    private final SecurityUtils securityUtils;

    @Override
    public void validForView(FilmCollectionEntity entity) {
        super.validForView(entity);
        requireModeratorRoleIfNotOwner(entity);
    }

    @Override
    public void validForUpdate(FilmCollectionEntity entity) {
        super.validForUpdate(entity);
        requireModeratorRoleIfNotOwner(entity);
    }

    @Override
    public void validForDelete(FilmCollectionEntity entity) {
        super.validForDelete(entity);
        requireModeratorRoleIfNotOwner(entity);
    }

    private void requireModeratorRoleIfNotOwner(FilmCollectionEntity entity) {
        Integer currentUserId = securityUtils.getCurrentUserId();
        if (Objects.equals(currentUserId, entity.getOwnerId())) return;
        securityUtils.requireRole(UserRole.MODERATOR);
    }

}
