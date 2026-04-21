package cz.osu.backend.model.dto.course;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class ReviewResponseDTO {
    UUID id;
    int rating;
    String username;
    String comment;
    String course;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}
