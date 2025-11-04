package ua.edu.ukma.springers.rezflix.controllers.ui;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import ua.edu.ukma.springers.rezflix.services.UserService;

@Controller
@RequiredArgsConstructor
public class UiPagesController {

    private final UserService userService;

    @GetMapping("/login")
    public String getLoginPage() {
        return "login";
    }

    @GetMapping("/profile")
    public String getProfilePage(Model model) {
        model.addAttribute("user", userService.getCurrentUserInfo());
        return "profile";
    }
}
