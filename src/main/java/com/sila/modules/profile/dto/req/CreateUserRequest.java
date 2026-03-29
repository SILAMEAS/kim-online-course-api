package com.sila.modules.profile.dto.req;

import com.sila.share.enums.ROLE;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateUserRequest {
  @Email @NotBlank private String email;

  @NotBlank private String password;

  @NotBlank private String firstName;

  @NotBlank private String lastName;

  @NotNull
  @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
  private ROLE role;
}
