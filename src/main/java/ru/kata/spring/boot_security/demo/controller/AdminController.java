package ru.kata.spring.boot_security.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.service.UserService;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping(value = "/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {
    private final UserService service;

    @Autowired
    public AdminController(UserService service) {
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

    @GetMapping("/add")
    public String addUser(Model model, Principal principal) {
        User currentUser = service.findByUsername(principal.getName());
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("allRoles", service.listRoles());
        model.addAttribute("username", service.usernameGenerator());
        return "add";
    }

    @PostMapping("/delete")
    public String deleteUser(@ModelAttribute("id") Long id) {
        service.deleteUserById(id);
        return "redirect:/admin/";
    }

    @PostMapping("/update")
    public String updateUser(@ModelAttribute User user, @RequestParam(value = "roles", required = false) List<String> roleNames) {
        user.setRoles(service.convertRoles(roleNames));
        service.updateUser(user);
        return "redirect:/admin/";
    }

    @PostMapping("/save")
    public String saveUser(@ModelAttribute User user, @RequestParam(value = "roles") List<String> roleNames) {
        user.setRoles(service.convertRoles(roleNames));
        service.add(user);
        return "redirect:/admin/";
    }
}