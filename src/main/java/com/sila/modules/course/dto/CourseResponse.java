package com.sila.modules.course.dto;

import com.sila.modules.profile.dto.res.UserResponse;
import com.sila.share.enums.CourseStatus;
import com.sila.share.enums.LevelStatus;
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
  private String imageUrl;
  private LevelStatus level;
  private CourseStatus status;
  private String category;
  private Double rating;
  private Number reviewsCount;
  private Number duration;
  private Number studentsCount;
  private UserResponse instructor;
}
