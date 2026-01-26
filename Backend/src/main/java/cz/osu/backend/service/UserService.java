package cz.osu.backend.service;

import cz.osu.backend.exception.ResourceNotFoundException;
import cz.osu.backend.model.db.Course;
import cz.osu.backend.model.db.User;
import cz.osu.backend.model.json.EnrollmentDTO;
import cz.osu.backend.model.json.UserRequestDTO;
import cz.osu.backend.repository.CourseRepository;
import cz.osu.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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

    public User getUserById(UUID id) {
        return userRepository.getUserById(id).orElseThrow(() -> new UsernameNotFoundException("Uživatel nenalezen: " + id));
    }

    public User getUserByUsername(String username) {
        return userRepository.getUserByUsername(username).orElseThrow(() -> new UsernameNotFoundException("Uživatel nenalezen: " + username));
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

    public void enrollUserToCourse(EnrollmentDTO request) {
        User user = getUserById(request.getUserId());
        Course course = courseRepository.findById(request.getCourse()).orElseThrow(() -> new ResourceNotFoundException("Course not found"));
        user.getCourses().add(course);
        userRepository.save(user);
    }

    public void unenrollUserFromCourse(EnrollmentDTO request) {
        User user = getUserById(request.getUserId());
        Course course = courseRepository.findById(request.getCourse()).orElseThrow(() -> new ResourceNotFoundException("Course not found"));
        user.getCourses().remove(course);
        userRepository.save(user);
    }
}
