package ua.edu.ukma.springers.rezflix.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ua.edu.ukma.springers.rezflix.domain.entities.UserEntity;
import ua.edu.ukma.springers.rezflix.domain.enums.UserRole;
import ua.edu.ukma.springers.rezflix.mappers.EnumsMapper;
import ua.edu.ukma.springers.rezflix.repositories.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    private final SuperAdminProperties superAdminProperties;
    private final EnumsMapper enumsMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if (superAdminProperties.getLogin().equals(username))
            return getSuperAdminDetails();
        else
            return getUserDetails(username);
    }

    private UserDetails getSuperAdminDetails() {
        return new User(
            superAdminProperties.getLogin(), superAdminProperties.getPasswordHash(),
            List.of(new SimpleGrantedAuthority(enumsMapper.mapToString(UserRole.SUPER_ADMIN)))
        );
    }

    private User getUserDetails(String username) throws UsernameNotFoundException {
        UserEntity user = userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException(username));
        return new User(
            user.getUsername(), user.getPasswordHash(),
            List.of(new SimpleGrantedAuthority(enumsMapper.mapToString(user.getType())))
        );
    }
}
