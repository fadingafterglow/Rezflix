package ua.edu.ukma.springers.rezflix.controllers.ui;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import ua.edu.ukma.springers.rezflix.services.FilmService;
import ua.edu.ukma.springers.rezflix.services.UserService;
import ua.edu.ukma.springers.rezflix.controllers.rest.model.UpsertFilmDto;

@Controller
@RequiredArgsConstructor
public class UiPagesController {

    private final UserService userService;
    private final FilmService filmService;

    @GetMapping("/login")
    public String getLoginPage() {
        return "login";
    }

    @GetMapping("/profile")
    public String getProfilePage(Model model) {
        model.addAttribute("user", userService.getCurrentUserInfo());
        return "profile";
    }

    @PostMapping("/profile")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public String createFilm(UpsertFilmDto filmDto, Model model) {

        try {
            filmService.create(filmDto);
            model.addAttribute("filmAddedSuccess", true);
        } catch (Exception e) {
            model.addAttribute("filmAddedError", "Помилка додавання: " + e.getMessage());
        }

        model.addAttribute("user", userService.getCurrentUserInfo());
        return "profile";
    }
}
