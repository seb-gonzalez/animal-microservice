package com.challenge.controller;

import com.challenge.model.CaptureRequest;
import com.challenge.model.ImageResponse;
import com.challenge.repository.ImageRepository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/animals")
@Tag(name = "Animals", description = "API for capturing and retrieving animal images")
public class AnimalController {
    
	private final ImageRepository imageRepository;
    private final RestTemplate restTemplate;
    private static final Map<String, String> ANIMAL_APIS = new HashMap<>();
    static {
        ANIMAL_APIS.put("cats", "https://placekitten.com/300/300");
        ANIMAL_APIS.put("dogs", "https://place.dog/300/300");
        ANIMAL_APIS.put("bears", "https://placebear.com/300/300");
    }

    public AnimalController(ImageRepository imageRepository) {
        this.imageRepository = imageRepository;
        this.restTemplate = new RestTemplate();
    }

    @Operation(summary = "Capture and store an image")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Image successfully captured and stored"),
        @ApiResponse(responseCode = "400", description = "Invalid request data"),
        @ApiResponse(responseCode = "404", description = "Resource not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error while processing the image")
    })
    @PostMapping("/capture")
    public ResponseEntity<Object> captureAnimals(@RequestBody CaptureRequest request) {
    	
        String animal = request.getAnimal() != null ? request.getAnimal().toLowerCase() : null;
        int count = request.getCount();

        if (animal == null || animal.isEmpty() || count <= 0) {
            
        	return ResponseEntity.badRequest().body(Map.of("error", "Missing or invalid animal/count"));
            
        }

        if (!ANIMAL_APIS.containsKey(animal)) {
        	
            return ResponseEntity.badRequest().body(Map.of("error", "Unsupported animal type. Use 'cats', 'dogs', or 'bears'"));
        }

        try {
        	
            List<ImageResponse> images = new ArrayList<>();
            String baseUrl = ANIMAL_APIS.get(animal);

            // Fetch and store images
            for (int i = 0; i < count; i++) {
                String url = baseUrl + "?v=" + System.currentTimeMillis() + i;
                images.add(new ImageResponse(animal, url, Instant.now().toString()));
            }
            
           // Save all at once (better for performance)
            images = imageRepository.saveAll(images);


            // Build response list explicitly
            List<Map<String, Object>> responseImages = new ArrayList<>();
            for (ImageResponse img : images) {
            	
                Map<String, Object> imageMap = new HashMap<>();
                imageMap.put("id", img.getId().toString());
                imageMap.put("url", img.getUrl());
                imageMap.put("stored_at", img.getStoredAt());
                responseImages.add(imageMap);
            }

            // Build final response
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Successfully captured " + count + " " + animal + " images");
            response.put("images", responseImages);

            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to capture " + animal + " images"));
        }
    }

   
    @Operation(summary = "Get the latest image for a specific animal")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved the latest image"),
        @ApiResponse(responseCode = "404", description = "No images found for the animal")
    })
    @GetMapping("/{animalType}/latest")
    public ResponseEntity<Object> getLatestImage(@PathVariable String animalType) {
        
    	String animal = animalType != null ? animalType.toLowerCase() : null;
        List<ImageResponse> images = imageRepository.findByAnimal(animal);

        if (images == null || images.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", "No images found for animal type '" + animal + "'"));
        }

        ImageResponse latest = images.stream()
            .sorted((a, b) -> b.getStoredAt().compareTo(a.getStoredAt()))
            .findFirst()
            .get();

        // Build response explicitly 
        Map<String, Object> response = new HashMap<>();
        response.put("animal", animal);
        response.put("id", latest.getId().toString());
        response.put("url", latest.getUrl());
        response.put("stored_at", latest.getStoredAt());

        return ResponseEntity.ok(response);
    }
}