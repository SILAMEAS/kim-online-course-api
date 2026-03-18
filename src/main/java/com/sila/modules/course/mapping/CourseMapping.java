package com.sila.modules.course.mapping;

import com.sila.modules.course.dto.CourseDetailResponse;
import com.sila.modules.course.dto.CourseResponse;
import com.sila.modules.course.model.Course;
import com.sila.modules.image.service.ImageService;
import com.sila.modules.profile.dto.res.UserResponse;
import org.springframework.stereotype.Component;

@Component
public class CourseMapping {
  private final ImageService imageService;

  public CourseMapping(ImageService imageService) {
    this.imageService = imageService;
  }

  public CourseDetailResponse mapToCourseDetailResponse(Course course) {
    var instructor = course.getInstructor();
    return CourseDetailResponse.builder()
        .id(course.getId())
        .title(course.getTitle())
        .description(course.getDescription())
        .price(course.getPrice())
        .imageUrl(imageService.getUrlImage(course.getImage().getPublicId()))
        .instructor(
            UserResponse.builder()
                .id(instructor.getId())
                .firstName(instructor.getFirstName())
                .lastName(instructor.getLastName())
                .email(instructor.getEmail())
                .role(instructor.getRole())
                .imageUrl(
                    instructor.getImage() == null
                        ? null
                        : imageService.getUrlImage(instructor.getImage().getPublicId()))
                .build())
        .build();
  }

  public CourseResponse mapToCourseResponse(Course course) {
    return CourseResponse.builder()
        .id(course.getId())
        .title(course.getTitle())
        .description(course.getDescription())
        .price(course.getPrice())
        .imageUrl(imageService.getUrlImage(course.getImage().getPublicId()))
        .build();
  }
}
