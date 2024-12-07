package org.example.eventsphere.controller;
import java.util.List;
import java.util.Optional;

import org.example.eventsphere.model.Webinar;
import org.example.eventsphere.service.WebinarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/webinars")
public class WebinarController {

    @Autowired
    private WebinarService webinarService;

    @GetMapping
    public List<Webinar> getAllWebinars() {
        return webinarService.getAllWebinars();
    }

    @GetMapping("/{webinarId}")
    public Optional<Webinar> getWebinarByWebinarId(@PathVariable String webinarId) {
        return webinarService.getWebinarByWebinarId(webinarId);
    }

    @GetMapping("/instructor/{instructorUsername}")
    public List<Webinar> getWebinarsByInstructorUsername(@PathVariable String instructorUsername) {
        return webinarService.getWebinarsByInstructorUsername(instructorUsername);
    }
//
//    @GetMapping("/schedule")
//    public List<Webinar> getScheduledWebinars() {
//        return webinarService.getScheduledWebinars();
//    }

    @GetMapping("/completed")
    public List<Webinar> getCompletedWebinars() {
        return webinarService.getCompletedWebinars();
    }

    @GetMapping("/ongoing")
    public List<Webinar> getOngoingWebinars() {
        return webinarService.getOngoingWebinars();
    }

    @GetMapping("/{webinarId}/videoUrl")
    public String getWebinarVideoUrl(@PathVariable String webinarId) {
        Optional<Webinar> webinar = webinarService.getWebinarByWebinarId(webinarId);
        if (webinar.isPresent()) {
            return webinar.get().getVideoUrl();
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Webinar not found");
        }
    }

    @PostMapping
    public Webinar createWebinar(@RequestBody Webinar webinar) {
        return webinarService.createWebinar(webinar);
    }

    @PutMapping("/{id}")
    public Webinar updateWebinar(@PathVariable String id, @RequestBody Webinar webinar) {
        return webinarService.updateWebinar(id, webinar);
    }

    @PutMapping("/start/{webinarId}")
    public Webinar startWebinar(@PathVariable String webinarId) {
        return webinarService.startWebinar(webinarId);
    }

    @PutMapping("/end/{webinarId}")
    public Webinar endWebinar(@PathVariable String webinarId) {
        return webinarService.endWebinar(webinarId);
    }

    @DeleteMapping("/{id}")
    public void deleteWebinar(@PathVariable String id) {
        webinarService.deleteWebinar(id);
    }

    @PutMapping("/{webinarId}/decrementSlots")
    public void decrementSlots(@PathVariable String webinarId) {
        webinarService.decrementSlots(webinarId);
    }

    @PutMapping("/{webinarId}/incrementSlots")
    public void incrementSlots(@PathVariable String webinarId) {
        webinarService.incrementSlots(webinarId);
    }

}
