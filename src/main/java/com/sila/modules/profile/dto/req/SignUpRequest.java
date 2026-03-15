package com.sila.modules.profile.dto.req;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.io.File;
import lombok.Data;

@Data
public class SignUpRequest {
  @Email @NotBlank private String email;

  @NotBlank private String password;

  @NotBlank private String firstName;

  @NotBlank private String lastName;

  @NotNull private File profile_img;
}
