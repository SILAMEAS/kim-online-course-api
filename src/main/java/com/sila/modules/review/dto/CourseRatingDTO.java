package com.sila.modules.review.dto;

import java.util.Map;
import lombok.Builder;

@Builder(toBuilder = true)
public record CourseRatingDTO(Double average, Long total, Map<Integer, Long> breakdown) {}
