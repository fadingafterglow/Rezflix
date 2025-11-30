package ua.edu.ukma.springers.rezflix.validators;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import ua.edu.ukma.springers.rezflix.domain.entities.FileInfoEntity;
import ua.edu.ukma.springers.rezflix.domain.entities.UserEntity;
import ua.edu.ukma.springers.rezflix.domain.enums.FileType;
import ua.edu.ukma.springers.rezflix.domain.enums.UserRole;

import java.util.Objects;

@Component
public class UserAvatarValidator extends BaseFileValidator<UserEntity> {

    public UserAvatarValidator() {
        super("image/.{1,127}");
    }

    @Override
    public FileType supportedFileType() {
        return FileType.USER_AVATAR;
    }

    @Override
    public void validForUpload(MultipartFile file, int boundEntityId) {
        super.validForUpload(file, boundEntityId);
        requireModeratorRoleIfNotOwner(boundEntityId);
    }

    @Override
    public void validForDelete(FileInfoEntity fileInfo) {
        requireModeratorRoleIfNotOwner(fileInfo.getBoundEntityId());
    }

    private void requireModeratorRoleIfNotOwner(int boundEntityId) {
        Integer currentUserId = securityUtils.getCurrentUserId();
        if (Objects.equals(currentUserId, boundEntityId)) return;
        securityUtils.requireRole(UserRole.MODERATOR);
    }
}
