package com.example.registrationservice.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "registrations")
public class Registration {
    public static final String SEQUENCE_NAME = "registration_sequence";

    @Id
    private String id;
    private String registrationId;
    private String username;
    private String webinarId;
    private String status;

    // Getters and Setters

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getRegistrationId() {
        return registrationId;
    }
    public void setRegistrationId(String registrationId) {
        this.registrationId = registrationId;
    }
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getWebinarId() {
        return webinarId;
    }
    public void setWebinarId(String webinarId) {
        this.webinarId = webinarId;
    }
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }

}
