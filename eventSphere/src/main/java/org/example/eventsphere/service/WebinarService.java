package org.example.eventsphere.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.example.eventsphere.model.User;
import org.example.eventsphere.model.Webinar;
import org.example.eventsphere.repository.UserRepository;
import org.example.eventsphere.repository.WebinarRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class WebinarService {

    @Autowired
    private WebinarRepository webinarRepository;

    @Autowired
    private SequenceGeneratorService sequenceGeneratorService;

    @Autowired
    private UserRepository userRepository;

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

    private void validateInstructor(String instructorUsername) {
        Optional<User> userOpt = userRepository.findByUsername(instructorUsername);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (user.getUsername() == null || user.getUsername().isEmpty()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Instructor username is invalid");
            }
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Instructor not found");
        }
    }
}