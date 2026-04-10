package com.sila.modules.enrolment.controller;

import com.sila.modules.enrolment.dto.EnrollmentResponse;
import com.sila.modules.enrolment.service.EnrollmentService;
import com.sila.share.annotation.PreAuthorization;
import com.sila.share.core.pagination.EnrollmentsPageResponse;
import com.sila.share.core.pagination.ResponsePaginationHandler;
import com.sila.share.core.pagination.PaginationRequest;
import com.sila.share.enums.ROLE;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/enrollments")
@Tag(
    name = "Enrollment Management",
    description = "APIs for managing student enrollments in courses")
public class EnrollmentController {

  private final EnrollmentService enrollmentService;

  public EnrollmentController(EnrollmentService enrollmentService) {
    this.enrollmentService = enrollmentService;
  }

  /** Get all enrollments */
  @GetMapping
  @PreAuthorization({ROLE.ADMIN, ROLE.STUDENT})
  @Operation(
      summary = "Get all enrollments",
      description =
          "Retrieve a paginated list of all enrollments. ADMIN can view all enrollments, "
              + "STUDENT can view only their own enrollments.",
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "Enrollments retrieved successfully",
            content = @Content(schema = @Schema(implementation = EnrollmentsPageResponse.class))),
        @ApiResponse(responseCode = "403", description = "Access denied")
      })
  public ResponseEntity<ResponsePaginationHandler<EnrollmentResponse>> getAllEnrollments(
      @ParameterObject PaginationRequest paginationRequest) {

    return ResponseEntity.ok(enrollmentService.listAllEnrollment(null, paginationRequest));
  }

  /** Get enrollments by course */
  @GetMapping("/courses/{courseId}")
  @PreAuthorization({ROLE.ADMIN, ROLE.STUDENT})
  @Operation(
      summary = "Get enrollments for a specific course",
      description =
          "Retrieve a paginated list of enrollments for a given course ID. "
              + "ADMIN can view all enrollments, STUDENT can view only their own enrollments.",
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "Enrollments retrieved successfully",
            content = @Content(schema = @Schema(implementation = EnrollmentsPageResponse.class))),
        @ApiResponse(responseCode = "404", description = "Course not found"),
        @ApiResponse(responseCode = "403", description = "Access denied")
      })
  public ResponseEntity<ResponsePaginationHandler<EnrollmentResponse>> getAllEnrollmentsByCourse(
      @Parameter(description = "ID of the course", example = "1", required = true) @PathVariable
          Long courseId,
      @ParameterObject PaginationRequest paginationRequest) {

    return ResponseEntity.ok(enrollmentService.listAllEnrollment(courseId, paginationRequest));
  }

  /** Delete all enrollments by course */
  @DeleteMapping("/courses/{courseId}")
  @PreAuthorization({ROLE.ADMIN})
  @Operation(
      summary = "Delete all enrollments for a course",
      description =
          "Deletes all enrollments associated with a specific course. Only ADMIN users can perform this operation.",
      responses = {
        @ApiResponse(responseCode = "200", description = "Enrollments deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Course not found"),
        @ApiResponse(responseCode = "403", description = "Access denied")
      })
  public ResponseEntity<String> deleteAllByCourse(
      @Parameter(description = "ID of the course", example = "1", required = true) @PathVariable
          Long courseId) {

    return ResponseEntity.ok(enrollmentService.bulkDeleteByCourseId(courseId));
  }
}
