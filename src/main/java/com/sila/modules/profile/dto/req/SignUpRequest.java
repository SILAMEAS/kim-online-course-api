package com.sila.modules.profile.dto.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class SignUpRequest {
  @Email @NotBlank private String email;

  @NotBlank private String password;

  @NotBlank private String confirmPassword;

  @NotBlank private String firstName;

  @NotBlank private String lastName;

  @Schema(type = "string", format = "binary")
  @NotNull
  private MultipartFile file;
}
