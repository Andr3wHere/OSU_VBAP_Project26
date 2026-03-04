package cz.osu.backend.controller.user;

import cz.osu.backend.model.db.User;
import cz.osu.backend.model.dto.user.UserRequestDTO;
import cz.osu.backend.model.dto.user.UserResponseDTO;
import cz.osu.backend.service.UserService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    @GetMapping
    public Page<UserResponseDTO> getAllUsers(Pageable pageable) {
        return userService.getAllUsers(pageable);
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable UUID id) {
        return userService.getUserById(id);
    }

    @GetMapping("/name/{username}")
    public User getUserByUsername(@PathVariable String username) {
        return userService.getUserByUsername(username);
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
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{userId}/enroll/{courseId}")
    public ResponseEntity<Void> enrollUserToCourse(@PathVariable UUID userId, @PathVariable UUID courseId) {
        userService.enrollUserToCourse(userId, courseId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{userId}/unenroll/{courseId}")
    public ResponseEntity<Void> unenrollUserFromCourse(@PathVariable UUID userId, @PathVariable UUID courseId) {
        userService.unenrollUserFromCourse(userId, courseId);
        return ResponseEntity.noContent().build();
    }
}
