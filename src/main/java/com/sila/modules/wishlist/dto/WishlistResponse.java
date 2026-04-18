package com.sila.modules.wishlist.dto;

import com.sila.modules.course.dto.CourseResponse;
import com.sila.modules.profile.dto.res.UserResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WishlistResponse {
  @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
  private Long id;
  @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
  private CourseResponse course;
  @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
  private UserResponse user;
}