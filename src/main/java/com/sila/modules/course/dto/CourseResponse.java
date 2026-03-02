package com.sila.modules.course.dto;

import com.sila.modules.courseSection.model.Section;
import com.sila.modules.profile.model.User;
import com.sila.share.enums.CourseStatus;
import lombok.Builder;

import java.util.List;

/**
 * Course Response
 *
 */
@Builder(toBuilder = true)
public record CourseResponse(
        String id,
        String title,
        String description,
        Double price,
        CourseStatus status,
        User instructor,
        List<Section> sections
) {
}

