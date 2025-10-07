package ua.edu.ukma.springers.rezflix.utils;

import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;
import ua.edu.ukma.springers.rezflix.domain.entities.UserEntity;
import ua.edu.ukma.springers.rezflix.domain.enums.UserRole;
import ua.edu.ukma.springers.rezflix.exceptions.ForbiddenException;

// TODO: Implement actual security logic
@Component
@RequestScope
public class SecurityUtils {

    public void authenticated() {}

    public boolean hasRole(UserRole... roles) {
        return true;
    }

    public void requireRole(UserRole... roles) {
        if (!hasRole(roles))
            throw new ForbiddenException();
    }

    public int getCurrentUserId() {
        return 1;
    }
    public UserEntity getCurrentUser() {
        return null;
    }
}
