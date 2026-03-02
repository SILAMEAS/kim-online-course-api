package com.sila.modules.course.dto;

import com.sila.share.enums.CourseStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CreateCourseRequest {

    @NotNull(message = "title is required")
    private String title;

    @NotNull(message = "description is required")
    private String description;

    @NotNull(message = "price is required")
    private Double price;

    @NotNull(message = "status is required")
    private CourseStatus status;

    @NotNull(message = "instructor is required")
    private Long instructorId;

}
