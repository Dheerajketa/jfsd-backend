package org.example.userservice.controller;

import org.example.userservice.model.User;
import org.example.userservice.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public User registerUser(@RequestBody User user) {
        try {
            return userService.registerUser(user);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @PostMapping("/login")
    public User loginUser(@RequestParam String username, @RequestParam String password) {
        return userService.loginUser(username, password);
    }

    @GetMapping("/profile")
    public User getUserProfile(@RequestParam String username) {
        return userService.getUserProfileByUsername(username);
    }

    @PostMapping("/role/request")
    public User requestInstructorRole(@RequestParam String username) {
        return userService.requestInstructorRole(username);
    }

    @PutMapping("/role/change")
    public User changeUserRole(@RequestParam String username, @RequestParam String newRole) {
        return userService.changeUserRole(username, newRole);
    }

    @PutMapping("/status/change")
    public User changeUserStatus(@RequestParam String username, @RequestParam boolean enabled) {
        return userService.changeUserStatus(username, enabled);
    }

    @GetMapping("/all")
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/instructor-requests")
    public List<User> getUsersAppliedForInstructor() {
        return userService.getUsersAppliedForInstructor();
    }

    @GetMapping("/instructors")
    public List<User> getInstructors() {
        return userService.getInstructors();
    }

    @GetMapping("/regular-users")
    public List<User> getRegularUsers() {
        return userService.getRegularUsers();
    }
}