package com.sila.modules.course.dto;

import com.sila.modules.course.Enum.CourseStatus;
import com.sila.modules.course.Enum.LevelStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CreateCourseRequest {

  @NotNull(message = "title is required")
  private String title;

  @NotNull(message = "description is required")
  private String description;

  @NotNull(message = "price is required")
  private Double price;

  @NotNull(message = "status is required")
  private CourseStatus status;

  @NotNull(message = "level is required")
  private LevelStatus level;

  @NotNull(message = "instructor is required")
  private Long instructorId;

  @NotNull(message = "file is required")
  private MultipartFile file;

  @NotNull(message = "categoryId is required")
  private Long categoryId;
}
