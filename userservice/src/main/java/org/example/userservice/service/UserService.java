package org.example.userservice.service;

import org.example.userservice.model.User;
import org.example.userservice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    public User registerUser(User user) {
        if (userRepository.findByUsername(user.getUsername()) != null) {
            throw new IllegalArgumentException("Username already exists");
        }
        // No password encoding
        user.setRole("USER");
        user.setEnabled(true);
        return userRepository.save(user);
    }

    public User loginUser(String username, String password) {
        User user = userRepository.findByUsername(username);
        if (user != null && user.getPassword().equals(password) && user.isEnabled()) {
            return user;
        }
        return null;
    }

    public User getUserProfileByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public User requestInstructorRole(String username) {
        User user = userRepository.findByUsername(username);
        if (user != null) {
            user.setRole("INSTRUCTOR_REQUESTED");
            return userRepository.save(user);
        }
        return null;
    }

    public User changeUserRole(String username, String newRole) {
        User user = userRepository.findByUsername(username);
        if (user != null) {
            user.setRole(newRole);
            return userRepository.save(user);
        }
        return null;
    }

    public User changeUserStatus(String username, boolean enabled) {
        User user = userRepository.findByUsername(username);
        if (user != null) {
            user.setEnabled(enabled);
            return userRepository.save(user);
        }
        return null;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public List<User> getUsersAppliedForInstructor() {
        return userRepository.findAll().stream()
                .filter(user -> "INSTRUCTOR_REQUESTED".equals(user.getRole()))
                .collect(Collectors.toList());
    }

    public List<User> getInstructors() {
        return userRepository.findAll().stream()
                .filter(user -> "Instructor".equals(user.getRole()))
                .collect(Collectors.toList());
    }

    public List<User> getRegularUsers() {
        return userRepository.findAll().stream()
                .filter(user -> "USER".equals(user.getRole()))
                .collect(Collectors.toList());
    }
}