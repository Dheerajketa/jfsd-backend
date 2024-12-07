package org.example.eventsphere.controller;


import java.util.List;

import org.example.eventsphere.model.User;
import org.example.eventsphere.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/generate-otp")
    public ResponseEntity<?> generateOtp(@RequestParam String email) {
        userService.generateAndSendOtp(email);
        return new ResponseEntity<>("OTP sent to your email", HttpStatus.OK);
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOtp(@RequestParam String email, @RequestParam String otp) {
        if (userService.verifyOtp(email, otp)) {
            return new ResponseEntity<>("OTP verified successfully", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Invalid OTP", HttpStatus.UNAUTHORIZED);
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody User user, @RequestParam String otp) {
        if (userService.verifyOtp(user.getEmail(), otp)) {
            try {
                User registeredUser = userService.registerUser(user);
                return new ResponseEntity<>(registeredUser, HttpStatus.CREATED);
            } catch (IllegalArgumentException e) {
                return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
            }
        } else {
            return new ResponseEntity<>("Invalid OTP", HttpStatus.UNAUTHORIZED);
        }
    }

    @PostMapping("/create-admin")
    public ResponseEntity<?> createAdminUser(@RequestBody User user) {
        try {
            User adminUser = userService.createAdminUser(user);
            return new ResponseEntity<>(adminUser, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestParam String username, @RequestParam String password) {
        User user = userService.loginUser(username, password);
        if (user != null) {
            return new ResponseEntity<>(user, HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Invalid username or password", HttpStatus.UNAUTHORIZED);
        }
    }

    @GetMapping("/profile")
    public ResponseEntity<?> getUserProfile(@RequestParam String username) {
        User user = userService.getUserProfileByUsername(username);
        if (user != null) {
            return new ResponseEntity<>(user, HttpStatus.OK);
        } else {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/profile")
    public ResponseEntity<?> updateUserProfile(@RequestParam String username, @RequestBody User updatedUser) {
        User user = userService.updateUserProfile(username, updatedUser);
        if (user != null) {
            return new ResponseEntity<>(user, HttpStatus.OK);
        } else {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/role/request")
    public ResponseEntity<?> requestInstructorRole(@RequestParam String username) {
        User user = userService.changeUserRole(username, "INSTRUCTOR_REQUESTED");
        if (user != null) {
            return new ResponseEntity<>(user, HttpStatus.OK);
        } else {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/role/change")
    public ResponseEntity<?> changeUserRole(@RequestParam String username, @RequestParam String newRole) {
        User user = userService.changeUserRole(username, newRole);
        if (user != null) {
            return new ResponseEntity<>(user, HttpStatus.OK);
        } else {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/status/change")
    public ResponseEntity<?> changeUserStatus(@RequestParam String username, @RequestParam boolean enabled) {
        User user = userService.changeUserStatus(username, enabled);
        if (user != null) {
            return new ResponseEntity<>(user, HttpStatus.OK);
        } else {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/all")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @GetMapping("/instructor-requests")
    public ResponseEntity<List<User>> getUsersAppliedForInstructor() {
        List<User> users = userService.getUsersAppliedForInstructor();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @GetMapping("/instructors")
    public ResponseEntity<List<User>> getInstructors() {
        List<User> users = userService.getInstructors();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @GetMapping("/regular-users")
    public ResponseEntity<List<User>> getRegularUsers() {
        List<User> users = userService.getRegularUsers();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }
}