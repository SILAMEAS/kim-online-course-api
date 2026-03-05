package com.sila.modules.course.controller;

import com.sila.modules.course.dto.CourseDetailResponse;
import com.sila.modules.course.dto.CourseResponse;
import com.sila.modules.course.dto.CreateCourseRequest;
import com.sila.modules.course.service.CourseService;
import com.sila.modules.video.service.VideoService;
import com.sila.share.annotation.PreAuthorization;
import com.sila.share.core.pagination.EntityResponseHandler;
import com.sila.share.core.pagination.PaginationRequest;
import com.sila.share.enums.ROLE;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/courses")
public class CourseController {
  final CourseService courseService;
  final VideoService videoService;

  public CourseController(CourseService courseService, VideoService videoService) {
    this.courseService = courseService;
    this.videoService = videoService;
  }

  @GetMapping
  public ResponseEntity<EntityResponseHandler<CourseResponse>> getCourse(
      @ModelAttribute PaginationRequest request) {
    return new ResponseEntity<>(this.courseService.listCourse(request), HttpStatus.OK);
  }

  @PostMapping
  @PreAuthorization(value = {ROLE.ADMIN, ROLE.INSTRUCTOR})
  public ResponseEntity<CourseResponse> createCourse(
      @RequestBody @Valid CreateCourseRequest request) {
    return new ResponseEntity<>(this.courseService.createCourse(request), HttpStatus.OK);
  }

  @GetMapping("/{courseId}")
  public ResponseEntity<CourseDetailResponse> getCourseDetail(@PathVariable Long courseId) {

    return ResponseEntity.ok(this.courseService.courseDetail(courseId));
  }
}
