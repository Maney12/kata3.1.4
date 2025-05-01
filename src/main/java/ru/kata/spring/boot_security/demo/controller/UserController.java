package ru.kata.spring.boot_security.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.service.UserService;

import java.security.Principal;

@Controller
@PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
@RequestMapping("/user")
public class UserController {


    private final UserService userDetailService;

    @Autowired
    public UserController(UserService userDetailService) {
        this.userDetailService = userDetailService;
    }

    @GetMapping
    public String userPage(Principal principal, Model model) {
        User user = userDetailService.findByUsername(principal.getName());
        model.addAttribute("user", user);
        return "home";
    }
}
