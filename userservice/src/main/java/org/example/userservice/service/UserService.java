package org.example.userservice.service;

import org.example.userservice.model.User;
import org.example.userservice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JavaMailSender mailSender;

    private Map<String, String> otpStorage = new HashMap<>();

    public void generateAndSendOtp(String email) {
        String otp = String.format("%06d", new Random().nextInt(999999));
        otpStorage.put(email, otp);
        sendOtpEmail(email, otp);
    }

    public boolean verifyOtp(String email, String otp) {
        String storedOtp = otpStorage.get(email);
        return storedOtp != null && storedOtp.equals(otp);
    }

    private void sendOtpEmail(String email, String otp) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Your OTP Code");
        message.setText("Your OTP code is: " + otp);
        mailSender.send(message);
    }

    public User registerUser(User user) {
        if (userRepository.findByUsername(user.getUsername()) != null) {
            throw new IllegalArgumentException("Username already exists");
        }
        // No password encoding
        user.setRole("USER");
        user.setEnabled(true);
        return userRepository.save(user);
    }

    public User createAdminUser(User user) {
        if (userRepository.findByUsername(user.getUsername()) != null) {
            throw new IllegalArgumentException("Username already exists");
        }
        // No password encoding
        user.setRole("ADMIN");
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
                .filter(user -> "INSTRUCTOR".equals(user.getRole()))
                .collect(Collectors.toList());
    }

    public List<User> getRegularUsers() {
        return userRepository.findAll().stream()
                .filter(user -> "USER".equals(user.getRole()))
                .collect(Collectors.toList());
    }

    public User updateUserProfile(String username, User updatedUser) {
        User user = userRepository.findByUsername(username);
        if (user != null) {
            user.setEmail(updatedUser.getEmail());
            user.setPassword(updatedUser.getPassword());
            // Update other fields as necessary
            return userRepository.save(user);
        }
        return null;
    }
}