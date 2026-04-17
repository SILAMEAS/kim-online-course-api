package com.sila.modules.course.dto;

import com.sila.modules.course.Enum.LevelStatus;
import com.sila.share.core.pagination.PaginationRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

/** The pageable query and filter */
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CoursePaginationRequest extends PaginationRequest {
  @Schema(
      description = "Filter by minimum rating (1-5)",
      example = "4",
      type = "integer",
      format = "int64")
  private Double rating;

  @Schema(
      description = "Filter by course difficulty level",
      example = "BEGINNER",
      implementation = LevelStatus.class)
  private LevelStatus levelStatus;

  @Schema(
      description = "The unique ID of the category",
      example = "1",
      type = "integer",
      format = "int64")
  private Long categoryId;

  @Schema(
      description = "Minimum price for the course",
      example = "19.99",
      type = "number",
      format = "double")
  private Double minPrice;

  @Schema(
      description = "Maximum price for the course",
      example = "99.99",
      type = "number",
      format = "double")
  private Double maxPrice;

  @Schema(
      description = "The unique ID of the instructorId",
      example = "1",
      type = "integer",
      format = "int64")
  private Long instructorId;
}
