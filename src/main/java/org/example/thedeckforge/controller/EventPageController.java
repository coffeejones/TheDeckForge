package org.example.thedeckforge.controller;

import org.example.thedeckforge.entity.User;
import org.example.thedeckforge.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/event")
public class EventPageController {

    private final UserService userService;

    public EventPageController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public String eventPage(Model model, Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            User user = userService.findByEmail(authentication.getName());
            model.addAttribute("currentUserId", user.getId());
            model.addAttribute("userRole", authentication.getAuthorities()
                    .iterator().next().getAuthority());
        } else {
            model.addAttribute("currentUserId", -1);
            model.addAttribute("userRole", "");
        }
        return "event";
    }
}