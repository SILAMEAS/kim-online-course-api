package com.sila.modules.course.dto;

import com.sila.modules.course.Enum.CourseStatus;
import com.sila.modules.course.Enum.LevelStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateCourseRequest {

  private String title;

  private String description;

  private Double price;

  private CourseStatus status;

  private Long instructorId;

  private MultipartFile file;

  private Long categoryId;

  private LevelStatus level;
}
