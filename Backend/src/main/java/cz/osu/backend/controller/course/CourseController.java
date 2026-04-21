package cz.osu.backend.controller.course;

import cz.osu.backend.model.db.Course;
import cz.osu.backend.model.dto.course.CourseRequestDTO;
import cz.osu.backend.model.dto.course.CourseResponseDTO;
import cz.osu.backend.service.CourseService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/courses")
public class CourseController {
    private final CourseService courseService;

    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    @PostMapping
    public ResponseEntity<CourseResponseDTO> createCourse(@Valid @RequestBody CourseRequestDTO request) {
        try {
            return ResponseEntity.ok(courseService.createCourse(request));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping
    public Page<CourseResponseDTO> getAllCourses(Pageable pageable) {
        return courseService.getAllCourses(pageable);
    }

    @GetMapping("/{id}")
    public CourseResponseDTO getCourseById(@PathVariable UUID id) {
        return courseService.getCourseResponseById(id);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CourseResponseDTO> updateCourse(@PathVariable UUID id, @Valid @RequestBody CourseRequestDTO request) {
        try {
            return ResponseEntity.ok(courseService.updateCourse(id, request));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCourse(@PathVariable UUID id) {
        courseService.deleteCourse(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/teacher/{teacherId}")
    public List<CourseResponseDTO> getCoursesByTeacher(@PathVariable UUID teacherId) {
        return courseService.getCoursesByTeacher(teacherId);
    }
}
