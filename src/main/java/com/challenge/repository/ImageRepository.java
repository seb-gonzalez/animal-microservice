package com.challenge.repository;

import com.challenge.model.ImageResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;


/* The ImageRepository lets the app save and find animal-related image data in the H2 database, 
 * feeding all endpoints.
*/
public interface ImageRepository extends JpaRepository<ImageResponse, Long> {
    List<ImageResponse> findByAnimal(String animal);
}