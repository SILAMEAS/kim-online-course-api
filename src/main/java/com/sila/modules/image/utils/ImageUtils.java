package com.sila.modules.image.utils;

import com.sila.config.exception.BadRequestException;
import com.sila.modules.image.Enum.CloudinaryFolder;
import java.util.Arrays;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ImageUtils {

  public static boolean isValidCloudinaryFolder(String publicId) {
    if (publicId == null || publicId.isBlank()) {
      return false;
    }

    boolean valid =
        Arrays.stream(CloudinaryFolder.values())
            .map(folder -> folder.getValue() + "/")
            .anyMatch(publicId::startsWith);

    if (!valid) {
      throw new BadRequestException("Invalid Cloudinary folder: " + publicId);
    }

    return true;
  }

  public static String getCloudinaryPublicId(String path) {
    if (path == null) return null;

    int lastSlash = path.lastIndexOf("/");
    return lastSlash != -1 ? path.substring(lastSlash + 1) : path;
  }

  public static String resolvePublicIdForDeletion(String publicId) {
    if (publicId == null || publicId.isBlank()) {
      return null;
    }

    if (isValidCloudinaryFolder(publicId)) {
      return getCloudinaryPublicId(publicId);
    }

    return publicId;
  }

}
