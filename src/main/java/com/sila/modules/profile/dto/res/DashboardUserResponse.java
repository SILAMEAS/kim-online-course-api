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
public class DashboardUserResponse {
  @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
  private Long enrolled;

  @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
  private Long certificates;

  @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
  private Long timeComplete;

  @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
  private Long learningStreak;
}
