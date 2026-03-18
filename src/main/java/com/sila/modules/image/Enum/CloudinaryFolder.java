package com.sila.modules.image.Enum;

import java.util.Locale;
import lombok.Getter;

@Getter
public enum CloudinaryFolder {
  PROFILE("profile_images"),
  COURSE("course_images"),
  POST_IMAGE("post_images");
  private final String value;

  CloudinaryFolder(String keycloakClientRole) {
    this.value = keycloakClientRole.toLowerCase(Locale.ENGLISH);
  }
}
