package com.sila.modules.image.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.sila.config.exception.BadRequestException;
import java.io.IOException;
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
   * Uploads an image to Cloudinary under "profile_images" folder.
   *
   * @param file MultipartFile
   * @return Cloudinary publicId
   */
  public String uploadImage(MultipartFile file) {
    try {
      var uploadResult =
          cloudinary
              .uploader()
              .upload(
                  file.getBytes(),
                  ObjectUtils.asMap(
                      "folder", "profile_images",
                      "resource_type", "image",
                      "unique_filename", true));
      return uploadResult.get("public_id").toString();
    } catch (IOException e) {
      throw new BadRequestException("Image upload failed: " + e.getMessage());
    }
  }

  /** Deletes a Cloudinary image by its publicId. */
  public void deleteImage(String publicId) {
    if (publicId == null || publicId.isBlank()) return;

    try {
      var result = cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
      System.out.println("Delete result for " + publicId + ": " + result);
    } catch (IOException e) {
      throw new BadRequestException("Image delete failed: " + e.getMessage());
    }
  }

  /**
   * Updates an image: deletes old one and uploads new one.
   *
   * @param oldPublicId existing publicId (can be null)
   * @param newFile new file
   * @return new publicId
   */
  public String updateImage(String oldPublicId, MultipartFile newFile) {
    if (oldPublicId != null && !oldPublicId.isBlank()) {
      deleteImage(oldPublicId);
    }
    return uploadImage(newFile);
  }

  /** Generates a secure URL for a publicId. */
  public String getImageUrl(String publicId) {
    if (publicId == null) return null;
    return cloudinary.url().resourceType("image").publicId(publicId).secure(true).generate();
  }

  public List<String> uploadImages(List<MultipartFile> files) {
    return files.stream().map(this::uploadImage).toList();
  }

  public void deleteImages(List<String> publicIds) {

    if (publicIds == null || publicIds.isEmpty()) return;

    try {
      cloudinary.api().deleteResources(publicIds, ObjectUtils.asMap("resource_type", "image"));

    } catch (Exception e) {
      throw new BadRequestException("Bulk image delete failed: " + e.getMessage());
    }
  }
}
