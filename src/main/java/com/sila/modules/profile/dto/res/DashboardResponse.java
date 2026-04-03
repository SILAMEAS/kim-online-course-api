package com.sila.modules.profile.dto.res;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DashboardResponse {
  @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
  private Long totalUsers;

  @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
  private Long totalCourses;

  @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
  private Long totalVideos;

  @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
  private String totalRevenues;

  @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
  private Long totalEnrollments;

  @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
  private Long totalImages;
}
