package ru.kata.spring.boot_security.demo.data_loader;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.service.UserService;

import java.util.Set;

@Component
public class DataLoader implements CommandLineRunner {
    private final UserService userService;

    public DataLoader(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void run(String... args) {
        Role adminRole = userService.saveRole(new Role("ROLE_ADMIN"));
        Role userRole = userService.saveRole(new Role("ROLE_USER"));

        userService.add(new User("admin", "admin", "Admin", "Adminov", "admin@mail.com", Set.of(adminRole, userRole), 20));
        userService.add(new User("user", "user", "User", "Userov", "user@mail.com", Set.of(userRole), 35));
    }
}