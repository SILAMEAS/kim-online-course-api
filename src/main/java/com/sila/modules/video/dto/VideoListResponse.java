package com.sila.modules.video.dto;

import com.sila.modules.course.dto.CourseResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class VideoListResponse {

  private Long id;

  private String title;

  private String publicId;

  private Integer duration;

  private CourseResponse course;
}
