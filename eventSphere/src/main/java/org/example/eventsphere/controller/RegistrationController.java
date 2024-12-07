package org.example.eventsphere.controller;

import java.util.List;

import org.example.eventsphere.model.Registration;
import org.example.eventsphere.service.RegistrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/registrations")
public class RegistrationController {

    @Autowired
    private RegistrationService registrationService;

    @PostMapping
    public Registration register(@RequestParam String username, @RequestParam String webinarId) {
        return registrationService.register(username, webinarId);
    }

    @GetMapping
    public List<Registration> getAllRegistrations() {
        return registrationService.getAllRegistrations();
    }

    @GetMapping("/{registrationId}")
    public Registration getRegistrationById(@PathVariable String registrationId) {
        return registrationService.getRegistrationById(registrationId);
    }

    @DeleteMapping("/{id}")
    public void deleteRegistration(@PathVariable String id) {
        registrationService.deleteRegistration(id);
    }

    @PostMapping("/cancel")
    public void cancelRegistration(@RequestParam String username, @RequestParam String webinarId) {
        registrationService.cancelRegistration(username, webinarId);
    }

    @GetMapping("/webinar/{webinarId}")
    public List<Registration> getRegistrationsByWebinarId(@PathVariable String webinarId) {
        return registrationService.getRegistrationsByWebinarId(webinarId);
    }

    @GetMapping("/user/{username}")
    public List<Registration> getWebinarsByUsername(@PathVariable String username) {
        return registrationService.getWebinarsByUsername(username);
    }

    @GetMapping("/user/{username}/completed")
    public List<Registration> getCompletedWebinarsByUsername(@PathVariable String username) {
        return registrationService.getCompletedWebinarsByUsername(username);
    }
}