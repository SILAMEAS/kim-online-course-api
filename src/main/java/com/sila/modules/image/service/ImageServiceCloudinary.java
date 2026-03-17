package com.sila.modules.image.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.sila.config.exception.BadRequestException;
import com.sila.modules.image.Enum.CloudinaryFolder;
import com.sila.modules.image.constant.ConstantCloudinaryImage;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
public class ImageServiceCloudinary {

  private final Cloudinary cloudinary;

  /**
   * Uploads an image to Cloudinary in the specified folder.
   *
   * @param file Multipart file
   * @param folder Cloudinary folder enum
   * @return publicId
   */
  public String uploadImage(MultipartFile file, CloudinaryFolder folder) {
    try {
      var result =
          cloudinary
              .uploader()
              .upload(
                  file.getBytes(),
                  ObjectUtils.asMap(
                      ConstantCloudinaryImage.FOLDER,
                      folder.getValue(),
                      ConstantCloudinaryImage.RESOURCE_TYPE,
                      ConstantCloudinaryImage.FORMAT_IMAGE,
                      ConstantCloudinaryImage.UNIQUE_FILENAME,
                      true));

      return result.get(ConstantCloudinaryImage.PUBLIC_ID).toString();
    } catch (IOException e) {
      log.error("Cloudinary upload failed for folder {}: {}", folder.getValue(), e.getMessage(), e);
      throw new BadRequestException("Image upload failed: " + e.getMessage());
    }
  }

  /**
   * Deletes a single image. Automatically extracts Cloudinary ID if folder prefix exists.
   *
   * @param publicId the full publicId or just the id
   */
  public void deleteImage(String publicId) {
    if (publicId == null || publicId.isBlank()) return;

    try {
      cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
    } catch (IOException e) {
      log.error("Cloudinary delete failed for publicId {}: {}", publicId, e.getMessage(), e);
      throw new BadRequestException("Image delete failed: " + e.getMessage());
    }
  }

  /**
   * Updates an image: deletes old one (if exists) and uploads new one.
   *
   * @param oldPublicId old publicId (nullable)
   * @param newFile new image file
   * @param folder Cloudinary folder
   * @return new publicId
   */
  public String updateImage(String oldPublicId, MultipartFile newFile, CloudinaryFolder folder) {
    if (oldPublicId != null && !oldPublicId.isBlank()) {
      deleteImage(oldPublicId);
    }
    return uploadImage(newFile, folder);
  }

  /**
   * Generates a secure URL for a Cloudinary image.
   *
   * @param publicId publicId of the image
   * @return secure URL
   */
  public String getImageUrl(String publicId) {
    if (publicId == null) return null;
    return cloudinary
        .url()
        .resourceType(ConstantCloudinaryImage.FORMAT_IMAGE)
        .publicId(publicId)
        .secure(true)
        .generate();
  }

  /**
   * Deletes multiple images by publicId.
   *
   * @param publicIds list of publicIds
   */
  public void deleteImages(List<String> publicIds) {
    if (publicIds == null || publicIds.isEmpty()) return;

    try {
      cloudinary
          .api()
          .deleteResources(
              publicIds,
              ObjectUtils.asMap(
                  ConstantCloudinaryImage.RESOURCE_TYPE, ConstantCloudinaryImage.FORMAT_IMAGE));
    } catch (Exception e) {
      log.error("Bulk delete failed for {} images: {}", publicIds.size(), e.getMessage(), e);
      throw new BadRequestException("Bulk image delete failed: " + e.getMessage());
    }
  }
}
