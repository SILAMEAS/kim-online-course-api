package com.sila.modules.review.repository;

import com.sila.modules.review.dto.ReviewSummary;
import com.sila.modules.review.model.Review;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReviewRepository
    extends JpaRepository<Review, Long>, JpaSpecificationExecutor<Review> {
  @Query("SELECT AVG(r.rating) FROM Review r WHERE r.course.id = :courseId")
  Double getAverageRatingByCourseId(@Param("courseId") Long courseId);

  // Gets counts for each star level (5, 4, 3, 2, 1)
  @Query("SELECT new com.sila.modules.review.dto.ReviewSummary(r.rating, COUNT(r)) " +
      "FROM Review r WHERE r.course.id = :courseId " +
      "GROUP BY r.rating ORDER BY r.rating DESC")
  List<ReviewSummary> getReviewBreakdown(@Param("courseId") Long courseId);

  // Gets average and total count in one trip to the database
  @Query("SELECT AVG(r.rating), COUNT(r) FROM Review r WHERE r.course.id = :courseId")
  List<Object[]> getOverallStats(@Param("courseId") Long courseId);
}
