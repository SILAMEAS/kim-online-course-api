package com.sila.modules.review.dto;

import com.sila.modules.course.dto.ListCourseResponse;
import com.sila.modules.profile.dto.res.UserResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ReviewResponse {
  @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
  private Long id;
  @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
  private UserResponse user;
  @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
  private String title;
  @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
  private String comment;
  @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
  private ListCourseResponse course;
  @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
  private Integer rating;
  @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
  private Instant createdAt;
}
