package com.example.registrationservice.service;

import com.example.registrationservice.model.Registration;
import com.example.registrationservice.model.RegistrationResponse;
import com.example.registrationservice.repository.RegistrationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class RegistrationService {

    @Autowired
    private RegistrationRepository registrationRepository;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private SequenceGeneratorService sequenceGeneratorService;

    private static final String USER_SERVICE_URL = "https://userservice.up.railway.app/api/users/profile?username=";
    private static final String WEBINAR_SERVICE_URL = "https://webinarservice.up.railway.app/webinars/";

    public Registration register(String username, String webinarId) {
        try {
            // Check for duplicate registration
            Optional<Registration> existingRegistration = registrationRepository.findByUsernameAndWebinarId(username, webinarId);
            if (existingRegistration.isPresent()) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "User is already registered for this webinar");
            }

            // Fetch user details from user service
            ResponseEntity<String> userResponse = restTemplate.getForEntity(USER_SERVICE_URL + username, String.class);
            ObjectMapper userMapper = new ObjectMapper();
            JsonNode userRoot = userMapper.readTree(userResponse.getBody());
            String userId = userRoot.path("id").asText();

            // Fetch webinar details from webinar service
            ResponseEntity<String> webinarResponse = restTemplate.getForEntity(WEBINAR_SERVICE_URL + webinarId, String.class);
            ObjectMapper webinarMapper = new ObjectMapper();
            JsonNode webinarRoot = webinarMapper.readTree(webinarResponse.getBody());
            String webinarTitle = webinarRoot.path("title").asText();
            int slots = webinarRoot.path("slots").asInt();

            // Check if slots are available
            if (slots <= 0) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No slots available for this webinar");
            }

            // Decrement slots
            restTemplate.put(WEBINAR_SERVICE_URL + webinarId + "/decrementSlots", null);

            // Create a new registration
            Registration registration = new Registration();
            registration.setRegistrationId(String.valueOf(sequenceGeneratorService.generateSequence(Registration.SEQUENCE_NAME)));
            registration.setUsername(username);
            registration.setWebinarId(webinarId);
            registration.setStatus("REGISTERED");

            return registrationRepository.save(registration);
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            // Handle HTTP errors
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error fetching user or webinar details", e);
        } catch (JsonProcessingException e) {
            // Handle JSON processing errors
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error processing JSON response", e);
        } catch (Exception e) {
            // Handle other errors
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error during registration", e);
        }
    }

    public List<Registration> getAllRegistrations() {
        return registrationRepository.findAll();
    }

    public RegistrationResponse getRegistrationById(String registrationId) {
        try {
            Registration registration = registrationRepository.findByRegistrationId(registrationId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Registration not found"));

            // Fetch webinar details to get the slots remaining
            ResponseEntity<String> webinarResponse = restTemplate.getForEntity(WEBINAR_SERVICE_URL + registration.getWebinarId(), String.class);
            ObjectMapper webinarMapper = new ObjectMapper();
            JsonNode webinarRoot = webinarMapper.readTree(webinarResponse.getBody());
            int slotsRemaining = webinarRoot.path("slots").asInt();

            // Create a response object
            RegistrationResponse response = new RegistrationResponse();
            response.setId(registration.getId());
            response.setRegistrationId(registration.getRegistrationId());
            response.setUsername(registration.getUsername());
            response.setWebinarId(registration.getWebinarId());
            response.setStatus(registration.getStatus());
            response.setSlotsRemaining(slotsRemaining);

            return response;
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            // Handle HTTP errors
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error fetching webinar details", e);
        } catch (JsonProcessingException e) {
            // Handle JSON processing errors
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error processing JSON response", e);
        } catch (Exception e) {
            // Handle other errors
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error fetching registration details", e);
        }
    }

    public void deleteRegistration(String id) {
        registrationRepository.deleteById(id);
    }

    public void cancelRegistration(String username, String webinarId) {
        Optional<Registration> registration = registrationRepository.findByUsernameAndWebinarId(username, webinarId);
        if (registration.isPresent()) {
            registrationRepository.delete(registration.get());
            restTemplate.put(WEBINAR_SERVICE_URL + webinarId + "/incrementSlots", null);
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

    public List<JsonNode> getCompletedWebinarsByUsername(String username) {
        List<Registration> registrations = registrationRepository.findByUsername(username);
        return registrations.stream()
                .map(registration -> {
                    ResponseEntity<String> webinarResponse = restTemplate.getForEntity(WEBINAR_SERVICE_URL + registration.getWebinarId(), String.class);
                    ObjectMapper webinarMapper = new ObjectMapper();
                    try {
                        JsonNode webinarRoot = webinarMapper.readTree(webinarResponse.getBody());
                        return webinarRoot;
                    } catch (JsonProcessingException e) {
                        throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error processing JSON response", e);
                    }
                })
                .filter(webinar -> "Completed".equals(webinar.path("status").asText()))
                .collect(Collectors.toList());
    }
}
