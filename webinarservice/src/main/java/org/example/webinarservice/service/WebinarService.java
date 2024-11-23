package org.example.webinarservice.service;

import org.example.webinarservice.model.Webinar;
import org.example.webinarservice.repository.WebinarRepository;
import org.example.webinarservice.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.client.HttpClientErrorException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class WebinarService {

    @Autowired
    private WebinarRepository webinarRepository;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private SequenceGeneratorService sequenceGeneratorService;

    private static final String USER_SERVICE_URL = "http://localhost:8080/api/users/profile?username=";

    public List<Webinar> getAllWebinars() {
        return webinarRepository.findAll();
    }

    public Optional<Webinar> getWebinarByWebinarId(String webinarId) {
        return webinarRepository.findByWebinarId(webinarId);
    }

    public List<Webinar> getWebinarsByInstructorUsername(String instructorUsername) {
        return webinarRepository.findByInstructorUsername(instructorUsername);
    }

    public Webinar createWebinar(Webinar webinar) {
        validateInstructor(webinar.getInstructorUsername());
        webinar.setWebinarId(String.valueOf(sequenceGeneratorService.generateSequence(Webinar.SEQUENCE_NAME)));
        webinar.setVideoUrl(YouTubeUrlConverter.convertToEmbeddedUrl(webinar.getVideoUrl()));
        return webinarRepository.save(webinar);
    }

    public Webinar updateWebinar(String id, Webinar webinar) {
        validateInstructor(webinar.getInstructorUsername());
        webinar.setId(id);
        webinar.setVideoUrl(YouTubeUrlConverter.convertToEmbeddedUrl(webinar.getVideoUrl()));
        return webinarRepository.save(webinar);
    }

    public void deleteWebinar(String id) {
        webinarRepository.deleteById(id);
    }

    public Webinar startWebinar(String webinarId) {
        Optional<Webinar> webinarOpt = webinarRepository.findByWebinarId(webinarId);
        if (webinarOpt.isPresent()) {
            Webinar webinar = webinarOpt.get();
            webinar.setStatus("Ongoing");
            return webinarRepository.save(webinar);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Webinar not found");
        }
    }

    public Webinar endWebinar(String webinarId) {
        Optional<Webinar> webinarOpt = webinarRepository.findByWebinarId(webinarId);
        if (webinarOpt.isPresent()) {
            Webinar webinar = webinarOpt.get();
            webinar.setStatus("Completed");
            return webinarRepository.save(webinar);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Webinar not found");
        }
    }

    public List<Webinar> getOngoingWebinars() {
        return webinarRepository.findAll().stream()
                .filter(webinar -> "Ongoing".equals(webinar.getStatus()))
                .collect(Collectors.toList());
    }

    private void validateInstructor(String instructorUsername) {
        try {
            ResponseEntity<String> response = restTemplate.getForEntity(USER_SERVICE_URL + instructorUsername, String.class);
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response.getBody());
            String role = root.path("role").asText();
            if (!"Instructor".equals(role)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid instructor role");
            }
        } catch (HttpClientErrorException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid instructor username");
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error validating instructor");
        }
    }

    public List<Webinar> getScheduledWebinars() {
        return webinarRepository.findAll().stream()
                .filter(webinar -> "Upcoming".equals(webinar.getStatus()))
                .collect(Collectors.toList());
    }

    public List<Webinar> getCompletedWebinars() {
        return webinarRepository.findAll().stream()
                .filter(webinar -> "Completed".equals(webinar.getStatus()))
                .collect(Collectors.toList());
    }

    public void decrementSlots(String webinarId) {
        Optional<Webinar> webinarOpt = webinarRepository.findByWebinarId(webinarId);
        if (webinarOpt.isPresent()) {
            Webinar webinar = webinarOpt.get();
            if (webinar.getSlots() > 0) {
                webinar.setSlots(webinar.getSlots() - 1);
                webinarRepository.save(webinar);
            } else {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No slots available");
            }
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Webinar not found");
        }
    }

    public void incrementSlots(String webinarId) {
        Optional<Webinar> webinarOpt = webinarRepository.findByWebinarId(webinarId);
        if (webinarOpt.isPresent()) {
            Webinar webinar = webinarOpt.get();
            webinar.setSlots(webinar.getSlots() + 1);
            webinarRepository.save(webinar);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Webinar not found");
        }
    }
}