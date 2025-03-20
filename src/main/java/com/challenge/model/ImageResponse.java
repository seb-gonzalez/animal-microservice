package com.challenge.model;

import jakarta.persistence.*;

@Entity
@Table(name = "images")  // Defines table name
public class ImageResponse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-incremented ID
    private Long id; 

    private String url;
    private String storedAt;
    private String animal;  // Needed for querying images by animal

    public ImageResponse() {}

    public ImageResponse(String animal, String url, String storedAt) {
        this.animal = animal;
        this.url = url;
        this.storedAt = storedAt;
    }

    // Getters
    public Long getId() { return id; }
    public String getUrl() { return url; }
    public String getStoredAt() { return storedAt; }
    public String getAnimal() { return animal; }
}
