package com.sila.modules.video.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.sila.config.exception.BadRequestException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * Service for interacting with Cloudinary for video operations.
 *
 * <p>Supports uploading, updating, deleting single or multiple videos, and generating signed URLs
 * for secure video access.
 */
@Service
@RequiredArgsConstructor
public class ImageCloudinaryService {

  private final Cloudinary cloudinary;

  /**
   * Uploads an image to Cloudinary.
   *
   * @param file MultipartFile image
   * @return publicId of uploaded image
   */
  public String uploadImage(MultipartFile file) {
    try (InputStream inputStream = file.getInputStream()) {

      var uploadResult =
              cloudinary
                      .uploader()
                      .upload(
                              inputStream,
                              ObjectUtils.asMap(
                                      "resource_type", "image"
                              ));

      return uploadResult.get("public_id").toString();

    } catch (IOException e) {
      throw new BadRequestException("Image upload failed: " + e.getMessage());
    }
  }

  public List<String> uploadImages(List<MultipartFile> files) {
    return files.stream()
            .map(this::uploadImage)
            .toList();
  }

  public void deleteImage(String publicId) {
    try {
      cloudinary
              .uploader()
              .destroy(
                      publicId,
                      ObjectUtils.asMap("resource_type", "image"));
    } catch (IOException e) {
      throw new BadRequestException("Image delete failed: " + e.getMessage());
    }
  }

  public void deleteImages(List<String> publicIds) {

    if (publicIds == null || publicIds.isEmpty()) return;

    try {
      cloudinary
              .api()
              .deleteResources(
                      publicIds,
                      ObjectUtils.asMap("resource_type", "image"));

    } catch (Exception e) {
      throw new BadRequestException("Bulk image delete failed: " + e.getMessage());
    }
  }
  public String updateImage(String oldPublicId, MultipartFile newFile) {

    if (oldPublicId != null) {
      deleteImage(oldPublicId);
    }

    return uploadImage(newFile);
  }

  public String getImageUrl(String publicId) {

    return cloudinary
            .url()
            .resourceType("image")
            .publicId(publicId)
            .secure(true)
            .generate();
  }
}
