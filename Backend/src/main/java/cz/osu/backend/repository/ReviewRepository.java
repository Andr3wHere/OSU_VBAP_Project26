package cz.osu.backend.repository;

import cz.osu.backend.model.db.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ReviewRepository extends JpaRepository<Review, UUID> {
    List<Review> findAllByCourseId(UUID courseId);

    List<Review> findAllByUserId(UUID userId);
}
