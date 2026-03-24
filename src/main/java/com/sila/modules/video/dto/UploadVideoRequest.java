package com.sila.modules.video.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Setter
@Getter
public class UploadVideoRequest {
  private String title;

  @Schema(type = "string", format = "binary")
  @NotNull
  private MultipartFile file;
}
