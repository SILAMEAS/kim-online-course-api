package com.sila.modules.review.service;

import com.sila.config.context.UserContext;
import com.sila.config.exception.NotFoundException;
import com.sila.modules.course.repository.CourseRepository;
import com.sila.modules.review.dto.CourseRatingDTO;
import com.sila.modules.review.dto.ReviewRequest;
import com.sila.modules.review.dto.ReviewResponse;
import com.sila.modules.review.dto.ReviewSummary;
import com.sila.modules.review.model.Review;
import com.sila.modules.review.repository.ReviewRepository;
import com.sila.modules.review.spec.ReviewSpec;
import com.sila.share.core.crud.AbstractCrudCommon;
import com.sila.share.core.pagination.EntityResponseHandler;
import com.sila.share.core.pagination.PaginationRequest;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for managing course enrollments.
 *
 * <p>This service provides functionalities to:
 *
 * <ul>
 *   <li>Check user access to a course.
 *   <li>Create enrollments based on payment.
 *   <li>List enrollments with pagination and search filters.
 *   <li>Bulk delete enrollments by course.
 *   <li>Check if enrollments exist for a specific course.
 * </ul>
 */
@Service
public class ReviewService extends AbstractCrudCommon<Review, Long, ReviewRepository> {

  private final CourseRepository courseRepository;

  protected ReviewService(
      ReviewRepository baseRepository, ModelMapper mapper, CourseRepository courseRepository) {
    super(baseRepository, mapper);
    this.courseRepository = courseRepository;
  }

  @Transactional(readOnly = true)
  public EntityResponseHandler<ReviewResponse> listReviews(
      PaginationRequest paginationRequest, Long courseId) {

    final var pageable =
        super.toPageable(
            paginationRequest.getPage(),
            paginationRequest.getLimit(),
            paginationRequest.getSortBy(),
            String.valueOf(paginationRequest.getSortOrder()));

    var spec = ReviewSpec.search(paginationRequest.getSearch());

    Page<Review> pagePayments = super.findAll(spec, pageable);
    return new EntityResponseHandler<>(
        pagePayments.map(pt -> this.mapper.map(pt, ReviewResponse.class)));
  }

  @Transactional
  public ReviewResponse createReview(ReviewRequest request, Long courseId) {

    final var course =
        courseRepository
            .findById(courseId)
            .orElseThrow(() -> new NotFoundException("Course not found"));

    var review = new Review();
    review.setTitle(request.getTitle());
    review.setComment(request.getComment());
    review.setRating(request.getRating());
    review.setCourse(course);
    review.setUser(UserContext.getUser());

    super.save(review);

    course.setReviewsCount(super.baseRepository.count());
    var avg = super.baseRepository.getAverageRatingByCourseId(courseId);
    if (avg != null) {
      // Correct Java logic for 1 decimal place (e.g., 3.76 -> 3.8)
      double roundedAvg = Math.round(avg * 10.0) / 10.0;
      course.setRating(roundedAvg);
    } else {
      course.setRating(0.0);
    }
    courseRepository.save(course);

    return mapper.map(review, ReviewResponse.class);
  }

  public CourseRatingDTO getCourseRatingUI(Long courseId) {
    // 1. Fetch data from Repository
    List<ReviewSummary> summaries = super.baseRepository.getReviewBreakdown(courseId);
    List<Object[]> statsList = super.baseRepository.getOverallStats(courseId);

    Double average = 0.0;
    Long totalReviews = 0L;

    // 2. Extract stats safely (Handling the List<Object[]> return type)
    if (statsList != null && !statsList.isEmpty() && statsList.get(0)[1] != null) {
      Object[] stats = statsList.get(0);
      average = (stats[0] != null) ? (Double) stats[0] : 0.0;
      totalReviews = (Long) stats[1];
    }

    // 3. Initialize breakdown with 0s (ensures UI shows 4-stars: 0 even if empty)
    Map<Integer, Long> breakdown = new LinkedHashMap<>();
    for (int i = 5; i >= 1; i--) {
      breakdown.put(i, 0L);
    }

    // 4. Merge database counts into the breakdown map
    if (summaries != null) {
      summaries.forEach(s -> breakdown.put(s.starLevel(), s.count()));
    }

    return CourseRatingDTO.builder()
        .average(Math.round(average * 10.0) / 10.0)
        .total(totalReviews)
        .breakdown(breakdown)
        .build();
  }
}
