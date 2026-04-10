package com.sila.modules.course.controller;

import com.sila.modules.course.dto.CourseDetailResponse;
import com.sila.modules.course.dto.CourseResponse;
import com.sila.modules.course.dto.CreateCourseRequest;
import com.sila.modules.course.dto.UpdateCourseRequest;
import com.sila.modules.course.service.CourseService;
import com.sila.share.annotation.PreAuthorization;
import com.sila.share.core.pagination.CoursePageResponse;
import com.sila.share.core.pagination.CoursePaginationRequest;
import com.sila.share.core.pagination.ResponsePaginationHandler;
import com.sila.share.core.pagination.PaginationRequest;
import com.sila.share.enums.ROLE;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/courses")
@Tag(name = "Course Management", description = "APIs for managing courses and course content")
public class CourseController {

  private final CourseService courseService;

  public CourseController(CourseService courseService) {
    this.courseService = courseService;
  }

  /** Get all courses */
  @GetMapping
  @Operation(
      summary = "Get all courses",
      description = "Retrieve a paginated list of courses. Supports pagination parameters.",
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "Courses retrieved successfully",
            content = @Content(schema = @Schema(implementation = CoursePageResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid pagination parameters")
      })
  public ResponseEntity<ResponsePaginationHandler<CourseResponse>> listAllCourses(
      @ParameterObject CoursePaginationRequest request) {

    return ResponseEntity.ok(courseService.lists(request));
  }

  /** Get all courses that user enrollment */
  @GetMapping("/student/{id}")
  @Operation(
      summary = "Get all courses",
      description = "Retrieve a paginated list of courses. Supports pagination parameters.",
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "Courses retrieved successfully",
            content = @Content(schema = @Schema(implementation = CoursePageResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid pagination parameters")
      })
  public ResponseEntity<ResponsePaginationHandler<CourseResponse>> listAllCoursesStudentEnrollment(
      @PathVariable Long id, @ParameterObject PaginationRequest request) {

    return ResponseEntity.ok(courseService.listCourseStudentEnrollment(request,id));
  }

  /** Create a new course */
  @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  @PreAuthorization({ROLE.ADMIN, ROLE.INSTRUCTOR})
  @Operation(
      summary = "Create a new course",
      description =
          "Creates a new course in the system. Only ADMIN or INSTRUCTOR users can perform this action.",
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "Course created successfully",
            content = @Content(schema = @Schema(implementation = CourseResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid course data"),
        @ApiResponse(responseCode = "403", description = "Access denied")
      })
  public ResponseEntity<CourseResponse> createCourse(
      @Valid @ModelAttribute CreateCourseRequest request) {

    return ResponseEntity.ok(courseService.create(request));
  }

  @PutMapping(value = "/{courseId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<CourseResponse> updateCourse(
      @Valid @ModelAttribute UpdateCourseRequest request, @PathVariable Long courseId) {
    return ResponseEntity.ok(courseService.update(courseId, request));
  }

  /** Get course details */
  @GetMapping("/{courseId}")
  @Operation(
      summary = "Get course details",
      description =
          "Retrieve detailed information of a course, including instructor info and course content.",
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "Course details retrieved successfully",
            content = @Content(schema = @Schema(implementation = CourseDetailResponse.class))),
        @ApiResponse(responseCode = "404", description = "Course not found")
      })
  public ResponseEntity<CourseDetailResponse> getCourseDetail(
      @Parameter(description = "ID of the course", example = "1", required = true) @PathVariable
          Long courseId) {

    return ResponseEntity.ok(courseService.detail(courseId));
  }

  /** Delete a course */
  @DeleteMapping("/{courseId}")
  @PreAuthorization(ROLE.ADMIN)
  @Operation(
      summary = "Delete a course",
      description =
          "Deletes a course by ID. Only ADMIN users can perform this. "
              + "All associated videos of the course will also be deleted.",
      responses = {
        @ApiResponse(responseCode = "200", description = "Course deleted successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied"),
        @ApiResponse(responseCode = "404", description = "Course not found")
      })
  public ResponseEntity<String> deleteCourseById(
      @Parameter(description = "ID of the course", example = "1", required = true) @PathVariable
          Long courseId) {

    courseService.deleteByCourseId(courseId);
    return ResponseEntity.ok("Course has been deleted successfully");
  }
}
