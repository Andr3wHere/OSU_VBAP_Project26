package cz.osu.backend.service;

import cz.osu.backend.exception.ResourceNotFoundException;
import cz.osu.backend.model.db.Course;
import cz.osu.backend.model.db.Review;
import cz.osu.backend.model.db.User;
import cz.osu.backend.model.dto.course.ReviewRequestDTO;
import cz.osu.backend.model.dto.course.ReviewResponseDTO;
import cz.osu.backend.repository.CourseRepository;
import cz.osu.backend.repository.ReviewRepository;
import cz.osu.backend.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class ReviewService {
    @Autowired
    ReviewRepository reviewRepository;
    @Autowired
    CourseRepository courseRepository;
    @Autowired
    UserRepository userRepository;

    public Review createReview(@Valid ReviewRequestDTO request) {
        Review review = new Review();
        review.setRating(request.getRating());
        review.setComment(request.getComment());
        Course course = courseRepository.findById(request.getCourseId()).orElseThrow(() -> new ResourceNotFoundException("Course not found"));
        User user = userRepository.findById(request.getUserId()).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        review.setCourse(course);
        review.setUser(user);
        review.setCreatedAt(LocalDateTime.now());
        return reviewRepository.save(review);
    }

    private ReviewResponseDTO getReviewResponse(Review review) {
        ReviewResponseDTO response = new ReviewResponseDTO();
        response.setId(review.getId());
        response.setRating(review.getRating());
        response.setUsername(review.getUser().getUsername());
        response.setComment(review.getComment());
        response.setCourse(review.getCourse().getName());
        response.setCreatedAt(review.getCreatedAt());
        response.setUpdatedAt(review.getUpdatedAt());

        return response;
    }

    public List<ReviewResponseDTO> getReviewsByCourse(UUID courseId) {
        List<Review> reviews = reviewRepository.findAllByCourseId(courseId);
        return reviews.stream().map(this::getReviewResponse).toList();
    }

    public Review getReviewById(UUID id) {
        return reviewRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Review not found"));
    }

    public ReviewResponseDTO updateReview(UUID id, @Valid ReviewRequestDTO request) {
        Review review = getReviewById(id);
        review.setRating(request.getRating());
        review.setComment(request.getComment());
        review.setUpdatedAt(LocalDateTime.now());
        reviewRepository.save(review);
        return getReviewResponse(review);
    }

    public void deleteReview(UUID id) {
        reviewRepository.deleteById(id);
    }

    public List<ReviewResponseDTO> getReviewsByUser(UUID userId) {
        List<Review> reviews = reviewRepository.findAllByUserId(userId);
        return reviews.stream().map(this::getReviewResponse).toList();
    }
}
