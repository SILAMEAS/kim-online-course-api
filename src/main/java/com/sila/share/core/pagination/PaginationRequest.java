package com.sila.share.core.pagination;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.*;
import org.springframework.data.domain.Sort;

/** The pageable query and filter */
@Builder
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PaginationRequest {
  @Builder.Default
  @Schema(name = "search", example = "Search field")
  private String search = PaginationConstant.DEFAULT_SEARCH;

  @Builder.Default
  @Min(1)
  @Schema(name = "page", example = "1")
  private Integer page = PaginationConstant.DEFAULT_PAGE_NUMBER;

  @Builder.Default
  @Min(1)
  @Max(100)
  @Schema(name = "limit", example = "10")
  private Integer limit = PaginationConstant.DEFAULT_PAGE_SIZE;

  @Builder.Default
  @Schema(name = "sortBy", example = "id")
  private String sortBy = PaginationConstant.DEFAULT_SORT_BY_FIELD;

  @Builder.Default
  @Schema(name = "sortOrder", example = "DESC,ASC")
  private Sort.Direction sortOrder = Sort.Direction.DESC;
}
