package com.challenge.repository;

import com.challenge.model.ImageResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ImageRepository extends JpaRepository<ImageResponse, Long> {
    List<ImageResponse> findByAnimal(String animal);
}