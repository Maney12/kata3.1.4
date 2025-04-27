package ru.kata.spring.boot_security.demo.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.service.UserDetailService;

@Controller
@RequestMapping(value = "/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {
    final UserDetailService service;

    @Autowired
    public AdminController(UserDetailService service) {
        this.service = service;
    }

    @GetMapping("/")
    public String listUsers(Model model) {
        model.addAttribute("users", service.listUsers());
        return "users";
    }

    @PostMapping("/add")
    public String addUser(@ModelAttribute("user") User user) {
        service.add(user);
        return "redirect:/admin/";
    }

    @PostMapping("/delete")
    public String deleteUser(@ModelAttribute("id") Long id) {
        service.deleteUserById(id);
        return "redirect:/admin/";
    }

    @GetMapping("/edit")
    public String editUser(@ModelAttribute("id") Long id, Model model) {
        User user = service.findById(id);
        model.addAttribute("user", user);
        return "edit";
    }

    @PostMapping("/update")
    public String updateUser(@ModelAttribute("user") User user) {
        service.updateUser(user);
        return "redirect:/admin/";
    }


}