package com.challenge.controller;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import com.challenge.model.ImageResponse;
import com.challenge.repository.ImageRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;  // Use MockBean for the repository
import org.springframework.test.web.servlet.MockMvc;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@WebMvcTest(AnimalController.class)
public class AnimalControllerGetLatestImageTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean  // Mock the ImageRepository bean so it's injected into the controller
    private ImageRepository imageRepository;

    @Test
    public void testGetLatestImage() throws Exception {
        // Prepare the mock response (e.g., the latest image of "cats")
        ImageResponse latestImage = new ImageResponse("cats", "https://placekitten.com/200/300", "2025-03-20T10:00:00Z");
        given(imageRepository.findByAnimal("cats")).willReturn(List.of(latestImage));

        // Perform GET request
        mockMvc.perform(get("/api/animals/cats/latest"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.animal").value("cats"))
                .andExpect(jsonPath("$.url").value("https://placekitten.com/200/300"))
                .andExpect(jsonPath("$.stored_at").value("2025-03-20T10:00:00Z"));
    }

    @Test
    public void testGetLatestImageNotFound() throws Exception {
        // Mock no images for "elephants"
        given(imageRepository.findByAnimal("elephants")).willReturn(List.of());

        // Perform GET request
        mockMvc.perform(get("/api/animals/elephants/latest"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("No images found for animal type 'elephants'"));
    }
}
