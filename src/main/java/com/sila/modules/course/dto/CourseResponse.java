package com.sila.modules.course.dto;

import com.sila.modules.course.Enum.CategoryStatus;
import com.sila.modules.course.Enum.CourseStatus;
import com.sila.modules.course.Enum.LevelStatus;
import com.sila.modules.profile.dto.res.UserResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Course Response */
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Setter
@Getter
public class CourseResponse {
  @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
  private Long id;
  @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
  private String title;
  @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
  private String description;
  @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
  private Double price;
  @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
  private Long updatedBy;
  @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
  private Long createdBy;
  @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
  private String imageUrl;
  @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
  private LevelStatus level;
  @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
  private CourseStatus status;
  @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
  private CategoryStatus category;
  @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
  private Double rating;
  @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
  private Number reviewsCount;
  @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
  private Double duration;
  @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
  private Number studentsCount;
  @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
  private UserResponse instructor;
}
