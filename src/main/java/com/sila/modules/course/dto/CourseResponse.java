package com.sila.modules.course.dto;

import com.sila.modules.course.model.Course;
import com.sila.modules.courseSection.model.Section;
import com.sila.modules.profile.dto.res.UserResponse;
import com.sila.share.enums.CourseStatus;
import lombok.Builder;

import java.util.List;

/**
 * Course Response
 *
 */
@Builder(toBuilder = true)
public record CourseResponse(
        String title,
        String description,
        Double price,
        CourseStatus status,
        UserResponse instructor,
        List<Section> sections
) {
    public static CourseResponse from(Course course, UserResponse instructorResponse) {
        return CourseResponse.builder()
                .title(course.getTitle())
                .description(course.getDescription())
                .price(course.getPrice())
                .status(course.getStatus())
                .instructor(instructorResponse)
                .sections(course.getSections())
                .build();
    }
}

