package ru.kata.spring.boot_security.demo.dao;

import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;

import java.util.List;

public interface UserDao {
    void add(User user);

    List<User> listUsers();

    void updateUser(User user);

    void deleteUserById(long id);

    public User findById(long id);

    User findByUsername(String username);

    void saveRole(Role role);
}
