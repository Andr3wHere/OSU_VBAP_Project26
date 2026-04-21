package cz.osu.backend.service;

import cz.osu.backend.exception.ResourceNotFoundException;
import cz.osu.backend.model.db.Course;
import cz.osu.backend.model.dto.course.CourseRequestDTO;
import cz.osu.backend.model.dto.course.CourseResponseDTO;
import cz.osu.backend.repository.CategoryRepository;
import cz.osu.backend.repository.CourseRepository;
import cz.osu.backend.repository.UserRepository;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class CourseService {
    @Autowired
    CourseRepository courseRepository;
    @Autowired
    CategoryRepository categoryRepository;
    @Autowired
    UserRepository userRepository;

    public CourseResponseDTO createCourse(CourseRequestDTO request) throws BadRequestException {
        Course course = new Course();
        course.setName(request.getName());
        course.setDescription(request.getDescription());
        course.setTeacher(userRepository.findById(request.getTeacherId()).orElseThrow(() -> new BadRequestException("Teacher not found")));
        course.setCategory(categoryRepository.findById(request.getCategoryId()).orElseThrow(() -> new BadRequestException("Category not found")));

        return getCourseResponseDTO(course);
    }

    private CourseResponseDTO getCourseResponseDTO(Course course) {
        Course savedCourse = courseRepository.save(course);

        CourseResponseDTO response = new CourseResponseDTO();
        response.setId(savedCourse.getId());
        response.setName(savedCourse.getName());
        response.setDescription(savedCourse.getDescription());
        response.setCategoryName(savedCourse.getCategory().getName());
        response.setTeacherName(savedCourse.getTeacher().getUsername());

        return response;
    }

    public Page<CourseResponseDTO> getAllCourses(Pageable pageable) {
        Page<Course> coursePage = courseRepository.findAll(pageable);

        return coursePage.map(course -> {
            CourseResponseDTO dto = new CourseResponseDTO();
            dto.setId(course.getId());
            dto.setName(course.getName());
            dto.setDescription(course.getDescription());
            dto.setTeacherName(course.getTeacher().getUsername());

            if (course.getCategory() != null) {
                dto.setCategoryName(course.getCategory().getName());
            }
            return dto;
        });
    }

    public List<CourseResponseDTO> getCoursesByTeacher(UUID userId) {
        List<Course> courses = courseRepository.findAllByTeacherId(userId);
        return courses.stream().map(this::getCourseResponseDTO).toList();
    }

    public Course getCourseById(UUID id) {
        return courseRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Kurz s ID " + id + " nebyl nalezen"));
    }

    public CourseResponseDTO updateCourse(UUID id, CourseRequestDTO request) throws BadRequestException {
        Course course = getCourseById(id);
        course.setName(request.getName());
        course.setDescription(request.getDescription());
        course.setTeacher(userRepository.findById(request.getTeacherId()).orElseThrow(() -> new BadRequestException("Teacher not found")));
        return getCourseResponseDTO(course);
    }

    public void deleteCourse(UUID id) {
        if (!courseRepository.existsById(id)) {
            throw new ResourceNotFoundException("Kurz s ID " + id + " nebyl nalezen");
        }
        courseRepository.deleteById(id);
    }

    public CourseResponseDTO getCourseResponseById(UUID id) {
        return getCourseResponseDTO(getCourseById(id));
    }
}
