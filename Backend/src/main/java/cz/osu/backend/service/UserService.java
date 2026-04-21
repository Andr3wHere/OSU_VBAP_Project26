package cz.osu.backend.service;

import cz.osu.backend.exception.ResourceNotFoundException;
import cz.osu.backend.model.db.Course;
import cz.osu.backend.model.db.User;
import cz.osu.backend.model.dto.user.UserRequestDTO;
import cz.osu.backend.model.dto.user.UserResponseDTO;
import cz.osu.backend.repository.CourseRepository;
import cz.osu.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
public class UserService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.getUserByUsername(username).orElseThrow(() -> new UsernameNotFoundException("Uživatel nenalezen: " + username));

        return org.springframework.security.core.userdetails.User.withUsername(user.getUsername()).password(user.getPassword()).authorities(new ArrayList<>()).build();
    }

    public Page<UserResponseDTO> getAllUsers(Pageable pageable) {
        Page<User> userPage = userRepository.findAll(pageable);

        return userPage.map(user -> {
            UserResponseDTO dto = new UserResponseDTO();
            dto.setId(user.getId());
            dto.setUsername(user.getUsername());
            dto.setRole(user.getRole());

            return dto;
        });
    }

    public User getUserById(UUID id) {
        return userRepository.getUserById(id).orElseThrow(() -> new UsernameNotFoundException("Uživatel nenalezen: " + id));
    }

    public UserResponseDTO getUserByUsername(String username) {
        User user = userRepository.getUserByUsername(username).orElseThrow(() -> new UsernameNotFoundException("Uživatel nenalezen: " + username));
        UserResponseDTO response = new UserResponseDTO();
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setRole(user.getRole());

        return response;
    }

    public User updateUser(UUID id, UserRequestDTO request) {
        User user = getUserById(id);
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        return userRepository.save(user);
    }

    public void deleteUser(UUID id) {
        userRepository.deleteById(id);
    }

    public void enrollUserToCourse(UUID userId, UUID courseId) {
        User user = getUserById(userId);
        Course course = courseRepository.findById(courseId).orElseThrow(() -> new ResourceNotFoundException("Course not found"));
        user.getCourses().add(course);
        userRepository.save(user);
    }

    public void unenrollUserFromCourse(UUID userId, UUID courseId) {
        User user = getUserById(userId);
        Course course = courseRepository.findById(courseId).orElseThrow(() -> new ResourceNotFoundException("Course not found"));
        user.getCourses().remove(course);
        userRepository.save(user);
    }

    public List<String> getUserEnrollments(UUID userId) {
        User user = getUserById(userId);
        Set<Course> userCourses = user.getCourses();
        List<String> userCourseIds = new ArrayList<>();
        for (Course course : userCourses) {
            userCourseIds.add(course.getId().toString());
        }
        return userCourseIds;
    }
}
