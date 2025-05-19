package ru.kata.spring.boot_security.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kata.spring.boot_security.demo.dao.UserDaoImp;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class UserServiceImp implements UserService, UserDetailsService {
    private final UserDaoImp userDao;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImp(UserDaoImp userDao, PasswordEncoder passwordEncoder) {
        this.userDao = userDao;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    @Override
    public void add(User user) {
        if (user.getUsername() == null || user.getUsername().isEmpty()) {
            user.setUsername(usernameGenerator());
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userDao.add(user);
    }

    @Transactional(readOnly = true)
    @Override
    public List<User> listUsers() {
        return userDao.listUsers();
    }

    @Transactional
    @Override
    public void updateUser(User user) {

        if (user == null || user.getId() == null) {
            throw new IllegalArgumentException("Пользователь или его ID не могут быть null при обновлении");
        }
        User existingUser = userDao.findById(user.getId());
        if (existingUser == null) {
            throw new IllegalArgumentException("Пользователь с ID " + user.getId() + " не найден");
        }
        existingUser.setFirstName(user.getFirstName());
        existingUser.setLastName(user.getLastName());
        existingUser.setEmail(user.getEmail());
        existingUser.setAge(user.getAge());
        existingUser.setUsername(user.getUsername());
        existingUser.setRoles(user.getRoles());

        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(user.getPassword()));
        }

        userDao.updateUser(existingUser);
    }

    @Transactional
    @Override
    public void deleteUserById(long id) {
        userDao.deleteUserById(id);
    }

    @Transactional
    @Override
    public User findById(Long id) {
        return userDao.findById(id);
    }

    @Transactional
    @Override
    public User findByUsername(String username) {
        return userDao.findByUsername(username);
    }

    @Transactional
    @Override
    public Role saveRole(Role role) {
        userDao.saveRole(role);
        return role;
    }

    @Override
    public List<Role> listRoles() {
        return userDao.listRoles();
    }

    @Override
    public Set<Role> convertRoles(List<String> roleNames) {
        Set<Role> roles = new HashSet<>();
        if (roleNames != null && !roleNames.isEmpty()) {
            for (String roleName : roleNames) {
                Role role = listRoles().stream()
                        .filter(r -> r.getName().equals(roleName))
                        .findFirst()
                        .orElseThrow(() -> new IllegalArgumentException("Role not found: " + roleName));
                roles.add(role);
            }
            return roles;
        } else {
            roles.add(new Role("ROLE_USER"));
            return roles;
        }

    }

    @Override
    public String usernameGenerator() {
        List<User> users = userDao.listUsers();
        if (users.isEmpty()) {
            return "guest№1";
        }
        long id = users.get(users.size() - 1).getId() + 1;
        return "guest№" + id;
    }


    @Transactional
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found with username: " + username);
        }
        user.getRoles().size();
        return user;
    }
}