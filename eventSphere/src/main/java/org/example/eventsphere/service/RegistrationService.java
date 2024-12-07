package org.example.eventsphere.service;

import java.util.List;
import java.util.Optional;

import org.example.eventsphere.model.Registration;
import org.example.eventsphere.model.User;
import org.example.eventsphere.model.Webinar;
import org.example.eventsphere.repository.RegistrationRepository;
import org.example.eventsphere.repository.UserRepository;
import org.example.eventsphere.repository.WebinarRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class RegistrationService {

    @Autowired
    private RegistrationRepository registrationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private WebinarRepository webinarRepository;

    @Autowired
    private SequenceGeneratorService sequenceGeneratorService;

    public Registration register(String username, String webinarId) {
        // Check for duplicate registration
        Optional<Registration> existingRegistration = registrationRepository.findByUsernameAndWebinarId(username, webinarId);
        if (existingRegistration.isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "User is already registered for this webinar");
        }

        // Fetch user from local database
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (!userOpt.isPresent()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
        User user = userOpt.get();

        // Fetch webinar from local database
        Optional<Webinar> webinarOpt = webinarRepository.findByWebinarId(webinarId);
        if (!webinarOpt.isPresent()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Webinar not found");
        }
        Webinar webinar = webinarOpt.get();

        // Create new registration
        Registration registration = new Registration();
        registration.setId(String.valueOf(sequenceGeneratorService.generateSequence(Registration.SEQUENCE_NAME)));
        registration.setUsername(username);
        registration.setWebinarId(webinarId);

        return registrationRepository.save(registration);
    }

    public List<Registration> getAllRegistrations() {
        return registrationRepository.findAll();
    }

    public Registration getRegistrationById(String registrationId) {
        return registrationRepository.findById(registrationId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Registration not found"));
    }

    public void deleteRegistration(String id) {
        registrationRepository.deleteById(id);
    }

    public void cancelRegistration(String username, String webinarId) {
        Optional<Registration> registrationOpt = registrationRepository.findByUsernameAndWebinarId(username, webinarId);
        if (registrationOpt.isPresent()) {
            registrationRepository.delete(registrationOpt.get());
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Registration not found");
        }
    }

    public List<Registration> getRegistrationsByWebinarId(String webinarId) {
        return registrationRepository.findByWebinarId(webinarId);
    }

    public List<Registration> getWebinarsByUsername(String username) {
        return registrationRepository.findByUsername(username);
    }

    public List<Registration> getCompletedWebinarsByUsername(String username) {
        return registrationRepository.findByUsernameAndStatus(username, "Completed");
    }
}