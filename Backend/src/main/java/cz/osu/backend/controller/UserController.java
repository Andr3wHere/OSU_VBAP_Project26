package cz.osu.backend.controller;

import cz.osu.backend.model.db.User;
import cz.osu.backend.model.json.UserRequestDTO;
import cz.osu.backend.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable UUID id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @GetMapping("/name/{username}")
    public ResponseEntity<User> getUserByUsername(@PathVariable String username) {
        return ResponseEntity.ok(userService.getUserByUsername(username));
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable UUID id, @Valid @RequestBody UserRequestDTO request) {
        try {
            return ResponseEntity.ok(userService.updateUser(id, request));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{userId}/enroll/{courseId}")
    public ResponseEntity<Void> enrollUserToCourse(@Valid @RequestBody EnrollmentDTO request) {
        userService.enrollUserToCourse(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{userId}/unenroll/{courseId}")
    public ResponseEntity<Void> unenrollUserFromCourse(@Valid @RequestBody EnrollmentDTO request) {
        userService.unenrollUserFromCourse(request);
        return ResponseEntity.ok().build();
    }
}
