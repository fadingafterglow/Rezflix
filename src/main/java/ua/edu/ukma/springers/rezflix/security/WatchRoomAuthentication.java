package ua.edu.ukma.springers.rezflix.security;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import ua.edu.ukma.springers.rezflix.domain.enums.WatchRoomUserRole;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Getter
@RequiredArgsConstructor
public class WatchRoomAuthentication implements Authentication {

    private final String username;
    private final WatchRoomUserRole role;
    private final UUID roomId;

    @Override
    public String getPrincipal() {
        return StringUtils.isBlank(username) ? "ANONYMOUS" : username;
    }

    @Override
    public String getName() {
        return getPrincipal();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public UUID getDetails() {
        return roomId;
    }

    @Override
    public boolean isAuthenticated() {
        return true;
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
        throw new IllegalArgumentException();
    }
}
