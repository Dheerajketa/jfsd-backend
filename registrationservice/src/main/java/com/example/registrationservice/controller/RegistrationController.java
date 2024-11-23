package com.example.registrationservice.controller;

import com.example.registrationservice.model.Registration;
import com.example.registrationservice.model.RegistrationResponse;
import com.example.registrationservice.service.RegistrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public RegistrationResponse getRegistrationById(@PathVariable String registrationId) {
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
}
