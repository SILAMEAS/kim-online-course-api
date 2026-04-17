package com.sila.modules.course.dto;

import com.sila.modules.course.model.Category;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CategoryPageResponse {
  @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
  private List<Category> contents;

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
