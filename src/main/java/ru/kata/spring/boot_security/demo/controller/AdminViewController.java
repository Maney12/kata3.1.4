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
@RequestMapping(value = "/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminViewController {
    private final UserService service;

    @Autowired
    public AdminViewController(UserService service) {
        this.service = service;
    }

    @GetMapping("/")
    public String listUsers(Model model, Principal principal) {
        User currentUser = service.findByUsername(principal.getName());
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("users", service.listUsers());
        model.addAttribute("allRoles", service.listRoles());
        return "index";
    }
}