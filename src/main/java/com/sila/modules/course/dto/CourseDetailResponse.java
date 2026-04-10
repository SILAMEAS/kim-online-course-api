package com.sila.modules.course.dto;

import com.sila.modules.course.Enum.CourseStatus;
import com.sila.modules.course.Enum.LevelStatus;
import com.sila.modules.course.model.Category;
import com.sila.modules.profile.dto.res.UserResponse;
import com.sila.modules.video.dto.VideoListResponse;
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
public class CourseDetailResponse {
  private Long id;
  private String title;
  private String description;
  private Double price;
  private UserResponse instructor;
  private String imageUrl;
  private LevelStatus level;
  private CourseStatus status;
  private Category category;
  private Double rating;
  private Number reviewsCount;
  private Number duration;
  private Number studentsCount;
  private VideoListResponse curriculum;
}
