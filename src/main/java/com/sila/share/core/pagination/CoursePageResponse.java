package com.sila.share.core.pagination;

import com.sila.modules.course.dto.CourseResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CoursePageResponse {
  @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
  private List<CourseResponse> contents;
  @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
  private int page;
  @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
  private int limit;
  @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
  private long total;
  @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
  private int totalPage;
  @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
  private boolean hasNext;
}