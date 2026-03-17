package com.sila.modules.image.Enum;


import lombok.Getter;

import java.util.Locale;

@Getter
public enum CloudinaryFolder {
  PROFILE("profile_images"),
  POST_IMAGE("post_images");
  private final String value;

  CloudinaryFolder(String keycloakClientRole) {
    this.value = keycloakClientRole.toLowerCase(Locale.ENGLISH);
  }
}
