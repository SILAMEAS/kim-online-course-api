package com.sila.modules.profile.dto.req;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdatePasswordReq {
  @NotNull(message = "currentPassword is required")
  private String currentPassword;

  @NotNull(message = "newPassword is required")
  private String newPassword;

  private String confirmPassword;
}
