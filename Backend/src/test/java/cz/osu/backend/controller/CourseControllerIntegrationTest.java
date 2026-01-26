package cz.osu.backend.controller;

import cz.osu.backend.component.JwtUtil;
import cz.osu.backend.model.db.Category;
import cz.osu.backend.model.db.User;
import cz.osu.backend.model.json.UserRole;
import cz.osu.backend.repository.CategoryRepository;
import cz.osu.backend.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class CourseControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    void createCourse_WithoutToken_ShouldReturnUnauthorized() throws Exception {
        String newCourseJson = "{\"name\": \"Spring Boot\", \"description\": \"...\", \"category\": \"IT\", \"teacherId\": \"uuid-zde\"}";

        mockMvc.perform(post("/api/courses").contentType(MediaType.APPLICATION_JSON).content(newCourseJson)).andExpect(status().isForbidden());
    }

    @Test
    void createCourse_WithValidToken_ShouldReturnOk() throws Exception {
        User teacher = new User();
        teacher.setUsername("testAdmin");
        teacher.setPassword("heslo12345678");
        teacher.setRole(UserRole.TEACHER);
        userRepository.save(teacher);
        String token = jwtUtil.generateToken(teacher.getUsername());

        Category category = new Category();
        category.setName("Information Technology");
        categoryRepository.save(category);

        String newCourseJson = "{\"name\": \"Spring Boot\", \"description\": \"description\", \"categoryId\": \"" + category.getId() + "\", \"teacherId\": \"" + teacher.getId() + "\"}";

        mockMvc.perform(post("/api/courses").header("Authorization", "Bearer " + token).contentType(MediaType.APPLICATION_JSON).content(newCourseJson)).andDo(print()).andExpect(status().isOk());
    }
}