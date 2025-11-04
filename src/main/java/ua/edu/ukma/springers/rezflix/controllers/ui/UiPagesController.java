package ua.edu.ukma.springers.rezflix.controllers.ui;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import ua.edu.ukma.springers.rezflix.services.FilmService;
import ua.edu.ukma.springers.rezflix.services.UserService;
import ua.edu.ukma.springers.rezflix.controllers.rest.model.FilmCriteriaDto;

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


    @GetMapping("/")
    public String getLandingPage(Model model) {
        var films = filmService.getListResponseByCriteria(new FilmCriteriaDto());
        model.addAttribute("films", films.getItems());
        model.addAttribute("user", userService.getCurrentUserInfo());
        return "landing";
    }
}
