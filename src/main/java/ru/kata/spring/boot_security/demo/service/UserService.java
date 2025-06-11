package ru.kata.spring.boot_security.demo.service;

import org.springframework.security.core.userdetails.UserDetailsService;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;

import java.util.List;
import java.util.Set;

public interface UserService extends UserDetailsService {
    void add(User user);

    List<User> listUsers();

    void updateUser(User user);

    void deleteUserById(long id);

    User findById(Long id); // Изменено на Long

    User findByUsername(String username);

    Role saveRole(Role role);

    List<Role> listRoles();
}
