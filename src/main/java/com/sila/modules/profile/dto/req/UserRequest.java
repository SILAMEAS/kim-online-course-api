package com.sila.modules.profile.dto.req;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Setter
@Getter
public class UserRequest {
  private String firstName;
  private String lastName;
  private MultipartFile file;
}
