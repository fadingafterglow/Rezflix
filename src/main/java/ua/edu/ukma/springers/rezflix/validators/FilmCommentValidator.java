package ua.edu.ukma.springers.rezflix.validators;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ua.edu.ukma.springers.rezflix.domain.entities.FilmCommentEntity;
import ua.edu.ukma.springers.rezflix.domain.enums.UserRole;
import ua.edu.ukma.springers.rezflix.security.SecurityUtils;

import java.util.Objects;

@Component
@RequiredArgsConstructor
public class FilmCommentValidator extends BaseValidator<FilmCommentEntity> {

    private final SecurityUtils securityUtils;

    @Override
    public void validForUpdate(FilmCommentEntity entity) {
        super.validForUpdate(entity);
        requireModeratorRoleIfNotAuthor(entity);
    }

    @Override
    public void validForDelete(FilmCommentEntity entity) {
        super.validForDelete(entity);
        requireModeratorRoleIfNotAuthor(entity);
    }

    private void requireModeratorRoleIfNotAuthor(FilmCommentEntity entity) {
        Integer currentUserId = securityUtils.getCurrentUserId();
        if (Objects.equals(currentUserId, entity.getAuthorId())) return;
        securityUtils.requireRole(UserRole.MODERATOR);
    }
}
