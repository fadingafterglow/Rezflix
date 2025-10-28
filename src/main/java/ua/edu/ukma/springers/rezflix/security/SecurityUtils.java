package ua.edu.ukma.springers.rezflix.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;
import ua.edu.ukma.springers.rezflix.domain.entities.UserEntity;
import ua.edu.ukma.springers.rezflix.domain.enums.UserRole;
import ua.edu.ukma.springers.rezflix.exceptions.ForbiddenException;
import ua.edu.ukma.springers.rezflix.exceptions.UnauthenticatedException;
import ua.edu.ukma.springers.rezflix.mappers.EnumsMapper;
import ua.edu.ukma.springers.rezflix.repositories.UserRepository;

@Component
@RequestScope
@RequiredArgsConstructor
public class SecurityUtils {

    private final UserRepository userRepository;
    private final EnumsMapper enumsMapper;

    private UserRole userRole;
    private UserEntity user;

    public boolean isAuthenticated() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth instanceof UsernamePasswordAuthenticationToken && auth.isAuthenticated();
    }

    public void authenticated() {
        if (!isAuthenticated())
            throw new UnauthenticatedException();
    }

    public UserRole getUserRole() {
        if (userRole == null)
            userRole = loadRole();
        return userRole;
    }

    private UserRole loadRole() {
        if (isAuthenticated()) {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            return enumsMapper.mapRoleFromString(auth.getAuthorities().iterator().next().getAuthority());
        }
        return UserRole.ANONYMOUS;
    }

    public boolean hasRole(UserRole... roles) {
        UserRole role = getUserRole();
        for (UserRole r : roles) {
            if (role == r)
                return true;
        }
        return false;
    }

    public void requireRole(UserRole... roles) {
        if (!hasRole(roles))
            throw new ForbiddenException();
    }

    public UserEntity getCurrentUser() {
        if (user == null)
            user = loadUser();
        return user;
    }

    public Integer getCurrentUserId() {
        if (user == null)
            user = loadUser();
        return user != null ? user.getId() : null;
    }

    private UserEntity loadUser() {
        UserRole role = getUserRole();
        if (role == UserRole.SUPER_ADMIN || role == UserRole.ANONYMOUS) return null;
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username).orElse(null);
    }
}
