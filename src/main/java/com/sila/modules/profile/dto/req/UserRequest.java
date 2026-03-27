package com.sila.modules.profile.dto.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Setter
@Getter
public class UserRequest {
  @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
  private String firstName;

  @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
  private String lastName;

  private MultipartFile file;
}
