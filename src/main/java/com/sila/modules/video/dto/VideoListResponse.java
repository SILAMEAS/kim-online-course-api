package com.sila.modules.video.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class VideoListResponse {
  private String title;

  private String publicId;

  private Integer orderIndex;
}
