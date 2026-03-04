package com.sila.share.core.pagiation;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.*;
import org.springframework.data.domain.Sort;

/**
 * The pageable query and filter
 */
@Builder
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PaginationRequest {
    @Builder.Default
    private String search = PaginationConstant.DEFAULT_SEARCH;

    @Builder.Default
    @Min(1)
    private Integer page = PaginationConstant.DEFAULT_PAGE_NUMBER;

    @Builder.Default
    @Min(1)
    @Max(100)
    private Integer limit = PaginationConstant.DEFAULT_PAGE_SIZE;

    @Builder.Default
    private String sortBy = PaginationConstant.DEFAULT_SORT_BY_FIELD;
    @Builder.Default
    private Sort.Direction sortOrder = Sort.Direction.DESC;
}
