package com.sila.modules.course.controller;

import com.sila.modules.course.dto.CourseResponse;
import com.sila.modules.course.service.CourseService;
import com.sila.share.annotation.PreAuthorization;
import com.sila.share.core.EntityResponseHandler;
import com.sila.share.core.PaginationRequest;
import com.sila.share.enums.ROLE;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/course")
public class CourseController {
    final CourseService courseService;

    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }


    @PreAuthorization(value = {ROLE.ADMIN, ROLE.INSTRUCTOR})

    @GetMapping
    public ResponseEntity<EntityResponseHandler<CourseResponse>> getCourse(@ModelAttribute PaginationRequest request) {
        return new ResponseEntity<>(this.courseService.listCourse(request), HttpStatus.OK);
    }


}
