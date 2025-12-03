package ua.edu.ukma.springers.rezflix.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ua.edu.ukma.springers.rezflix.configuration.WebSecurityConfiguration;
import ua.edu.ukma.springers.rezflix.repositories.UserRepository;
import ua.edu.ukma.springers.rezflix.security.CustomUserDetailsService;
import ua.edu.ukma.springers.rezflix.security.JWTService;
import ua.edu.ukma.springers.rezflix.security.SecurityUtils;
import ua.edu.ukma.springers.rezflix.utils.DefaultMessageResolver;

@ActiveProfiles("test")
@Import({
        WebSecurityConfiguration.class,
        CustomUserDetailsService.class,
        JWTService.class,
        SecurityUtils.class,
        DefaultMessageResolver.class
})
public abstract class BaseControllerTest {

    @Autowired
    protected MockMvc mvc;
    @Autowired
    protected ObjectMapper objectMapper;
    @Autowired
    protected JWTService jwtService;
    @Autowired
    protected PasswordEncoder passwordEncoder;
    @MockitoBean
    protected UserRepository userRepository;
}
