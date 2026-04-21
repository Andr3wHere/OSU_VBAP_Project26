package cz.osu.backend.model.dto.course;

import lombok.Data;

import java.util.UUID;

@Data
public class CourseResponseDTO {
    private UUID id;
    private String name;
    private String description;
    private String categoryName;
    private String teacherName;
}
