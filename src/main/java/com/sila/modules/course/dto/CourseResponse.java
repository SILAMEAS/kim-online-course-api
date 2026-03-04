package com.sila.modules.course.dto;

import com.sila.modules.profile.dto.res.UserResponse;
import com.sila.share.enums.CourseStatus;
import lombok.Builder;

/** Course Response */
@Builder(toBuilder = true)
public record CourseResponse(
    String title, String description, Double price, CourseStatus status, UserResponse instructor) {}
