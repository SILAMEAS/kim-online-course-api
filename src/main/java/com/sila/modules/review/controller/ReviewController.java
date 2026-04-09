package com.sila.modules.review.controller;

import com.sila.modules.review.dto.CourseRatingDTO;
import com.sila.modules.review.dto.ReviewRequest;
import com.sila.modules.review.dto.ReviewResponse;
import com.sila.modules.review.service.ReviewService;
import com.sila.share.core.pagination.EntityResponseHandler;
import com.sila.share.core.pagination.PaginationRequest;
import com.sila.share.core.pagination.ReviewsPageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reviews")
@Tag(name = "Review Management", description = "APIs for managing student Review in courses")
public class ReviewController {

  private final ReviewService reviewService;

  public ReviewController(ReviewService reviewService) {
    this.reviewService = reviewService;
  }

  /** Get all review in course */
  @GetMapping("/course/{courseId}")
  @Operation(
      summary = "Get all course",
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "course retrieved successfully",
            content = @Content(schema = @Schema(implementation = ReviewsPageResponse.class))),
        @ApiResponse(responseCode = "403", description = "Access denied")
      })
  public ResponseEntity<EntityResponseHandler<ReviewResponse>> listReviews(
      @PathVariable Long courseId, @ParameterObject @Validated PaginationRequest request) {

    return ResponseEntity.ok(reviewService.listReviews(request, courseId));
  }

  /** create review in course */
  @PostMapping(value = "/course/{courseId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  @Operation(
      summary = "Get all course",
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "course retrieved successfully",
            content = @Content(schema = @Schema(implementation = ReviewsPageResponse.class))),
        @ApiResponse(responseCode = "403", description = "Access denied")
      })
  public ResponseEntity<ReviewResponse> createReviews(
      @PathVariable Long courseId, @Valid @ModelAttribute ReviewRequest request) {

    return ResponseEntity.ok(reviewService.createReview(request, courseId));
  }

  /** Get all review in course */
  @GetMapping("/course/{courseId}/rating")
  @Operation(
      summary = "Get all course",
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "course retrieved successfully",
            content = @Content(schema = @Schema(implementation = CourseRatingDTO.class))),
        @ApiResponse(responseCode = "403", description = "Access denied")
      })
  public ResponseEntity<CourseRatingDTO> getRating(@PathVariable Long courseId) {

    return ResponseEntity.ok(reviewService.getCourseRatingUI(courseId));
  }
}
