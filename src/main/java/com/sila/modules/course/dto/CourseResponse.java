package com.sila.modules.course.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Course Response */
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Setter
@Getter
public class CourseResponse {
  private Long id;
  private String title;
  private String description;
  private Double price;
  private Long updatedBy;
  private Long createdBy;
}
