package com.sila.modules.review.service;

import com.sila.config.context.UserContext;
import com.sila.config.exception.NotFoundException;
import com.sila.modules.course.repository.CourseRepository;
import com.sila.modules.review.dto.CourseRatingDTO;
import com.sila.modules.review.dto.ReviewRequest;
import com.sila.modules.review.dto.ReviewResponse;
import com.sila.modules.review.model.Review;
import com.sila.modules.review.repository.ReviewRepository;
import com.sila.modules.review.spec.ReviewSpec;
import com.sila.share.core.crud.AbstractCrudCommon;
import com.sila.share.core.pagination.PaginationRequest;
import com.sila.share.core.pagination.ResponsePaginationHandler;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.Map;

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
    public ResponsePaginationHandler<ReviewResponse> listReviews(
            PaginationRequest paginationRequest, Long courseId) {

        final var pageable =
                super.toPageable(
                        paginationRequest.getPage(),
                        paginationRequest.getLimit(),
                        paginationRequest.getSortBy(),
                        String.valueOf(paginationRequest.getSortOrder()));

        var spec = ReviewSpec.search(paginationRequest.getSearch());

        Page<Review> pagePayments = super.findAll(spec, pageable);
        return new ResponsePaginationHandler<>(
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

        //    update course
        course.setReviewsCount(super.baseRepository.count());
        var avg = super.baseRepository.findAll(ReviewSpec.hasCourseId(courseId)).size();
        if (avg != 0) {
            // Correct Java logic for 1 decimal place (e.g., 3.76 -> 3.8)
            double roundedAvg = Math.round(avg * 10.0) / 10.0;
            course.setRating(roundedAvg);
        } else {
            course.setRating(0.0);
        }
        courseRepository.save(course);

        return mapper.map(review, ReviewResponse.class);
    }

    @Transactional(readOnly = true)
    public CourseRatingDTO getCourseRatingUI(Long courseId) {

        var spec = ReviewSpec.hasCourseId(courseId);

        var stats = super.baseRepository.getOverallStats(spec);
        var summaries = super.baseRepository.getReviewBreakdown(spec);

        // Initialize 5 → 1 with 0
        Map<Integer, Long> breakdown = new LinkedHashMap<>();
        for (int i = 5; i >= 1; i--) {
            breakdown.put(i, 0L);
        }

        // Fill actual values
        summaries.forEach(s -> breakdown.put(s.starLevel(), s.count()));

        return CourseRatingDTO.builder()
                .average(round(stats.average()))
                .total(stats.total()) // ✅ THIS should be 6
                .breakdown(breakdown)
                .build();
    }

    private double round(double value) {
        return Math.round(value * 10.0) / 10.0;
    }
}
