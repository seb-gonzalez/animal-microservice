package com.challenge.controller;

import com.challenge.model.CaptureRequest;
import com.challenge.model.ImageResponse;
import com.challenge.repository.ImageRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AnimalController.class)
public class AnimalControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ImageRepository imageRepository; // ✅ Mocked repository

    @BeforeEach
    void setUp() {
        // ✅ Create mock ImageResponse objects
        List<ImageResponse> mockImages = Arrays.asList(
                new ImageResponse("cats", "https://example.com/image1.jpg", "2025-03-20"),
                new ImageResponse("cats", "https://example.com/image2.jpg", "2025-03-20"),
                new ImageResponse("cats", "https://example.com/image3.jpg", "2025-03-20")
        );

        when(imageRepository.save(any())).thenReturn(mockImages); // ✅ Return mock entity list
    }

    @Test
    public void testCaptureAnimalsValidRequest() throws Exception {
        CaptureRequest request = new CaptureRequest();
        request.setAnimal("cats");
        request.setCount(3);

        mockMvc.perform(post("/api/animals/capture")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("Successfully captured 3 cats images"))
                .andExpect(jsonPath("$.images").isArray())
                .andExpect(jsonPath("$.images.length()").value(0));
    }

    @Test
    public void testCaptureAnimalsInvalidAnimal() throws Exception {
        CaptureRequest request = new CaptureRequest();
        request.setAnimal("elephants");
        request.setCount(3);

        mockMvc.perform(post("/api/animals/capture")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Unsupported animal type. Use 'cats', 'dogs', or 'bears'"));
    }

    @Test
    public void testCaptureAnimalsMissingData() throws Exception {
        CaptureRequest request = new CaptureRequest();
        request.setAnimal("cats"); // ❌ Missing "count" field

        mockMvc.perform(post("/api/animals/capture")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Missing or invalid animal/count"));
    }
}
