package com.sila.modules.video.dto;

import com.sila.share.core.pagination.PaginationRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class VideoPaginationRequest extends PaginationRequest {
  private Long courseId;
}
