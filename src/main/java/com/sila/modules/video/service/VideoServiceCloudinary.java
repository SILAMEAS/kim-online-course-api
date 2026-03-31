package com.sila.modules.video.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.sila.config.exception.BadRequestException;
import com.sila.modules.video.constant.ConstantCloudinaryVideo;
import com.sila.share.constant.StaticMessage;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
public class VideoServiceCloudinary {

  private final Cloudinary cloudinary;

  /**
   * Uploads a video file to Cloudinary using a streaming approach (suitable for large files).
   *
   * @param file MultipartFile representing the video to upload
   * @return The publicId of the uploaded video
   * @throws BadRequestException if the upload fails
   */
  public String uploadVideo(MultipartFile file) {
    try (InputStream inputStream = file.getInputStream()) {
      var uploadResult =
          cloudinary
              .uploader()
              .uploadLarge(
                  inputStream,
                  ObjectUtils.asMap(
                      ConstantCloudinaryVideo.RESOURCE_TYPE,
                      ConstantCloudinaryVideo.RESOURCE_VIDEO,
                      ConstantCloudinaryVideo.CHUNK_SIZE,
                      ConstantCloudinaryVideo.CHUNK_MB));
      return uploadResult.get(ConstantCloudinaryVideo.PUBLIC_ID).toString();
    } catch (IOException e) {
      throw new BadRequestException(StaticMessage.VIDEO_UPLOAD_FAILED + e.getMessage());
    }
  }

  public Map<String, String> uploadVideoCustom(MultipartFile file) {
    try (InputStream inputStream = file.getInputStream()) {

      var uploadResult =
          cloudinary
              .uploader()
              .uploadLarge(
                  inputStream,
                  ObjectUtils.asMap(
                      ConstantCloudinaryVideo.RESOURCE_TYPE,
                      ConstantCloudinaryVideo.RESOURCE_VIDEO,
                      ConstantCloudinaryVideo.CHUNK_SIZE,
                      ConstantCloudinaryVideo.CHUNK_MB));

      String publicId = uploadResult.get(ConstantCloudinaryVideo.PUBLIC_ID).toString();

      Double durationDouble = (Double) uploadResult.get("duration"); // 👈 get duration
      String duration = durationDouble != null ? String.valueOf(durationDouble.intValue()) : "0";

      // ✅ use HashMap instead of singletonMap
      Map<String, String> result = new HashMap<>();
      result.put("publicId", publicId);
      result.put("duration", duration); // 👈 add this

      return result;

    } catch (IOException e) {
      throw new BadRequestException(StaticMessage.VIDEO_UPLOAD_FAILED + e.getMessage());
    }
  }

  /**
   * Generates a signed, time-limited URL for a video.
   *
   * @param publicId Public ID of the video
   * @return Signed URL for secure access
   */
  public String generateSignedUrl(String publicId) {
    return cloudinary
        .url()
        .resourceType(ConstantCloudinaryVideo.RESOURCE_VIDEO)
        .publicId(publicId)
        .signed(true)
        .generate();
  }

  /**
   * Deletes a single video from Cloudinary.
   *
   * @param publicId Public ID of the video to delete
   * @throws BadRequestException if deletion fails
   */
  public void deleteVideo(String publicId) {
    try {
      cloudinary
          .uploader()
          .destroy(
              publicId,
              ObjectUtils.asMap(
                  ConstantCloudinaryVideo.RESOURCE_TYPE, ConstantCloudinaryVideo.RESOURCE_VIDEO));
    } catch (IOException e) {
      throw new BadRequestException(StaticMessage.VIDEO_DELETE_FAILED + e.getMessage());
    }
  }

  /**
   * Updates a video by deleting the old one and uploading a new file.
   *
   * @param oldPublicId Public ID of the old video (nullable)
   * @param newFile New MultipartFile to upload
   * @return Public ID of the newly uploaded video
   */
  public String updateVideo(String oldPublicId, MultipartFile newFile) {
    if (oldPublicId != null) {
      deleteVideo(oldPublicId);
    }
    return uploadVideo(newFile);
  }

  /**
   * Generates a secure URL for viewing a video.
   *
   * @param publicId Public ID of the video
   * @return URL for watching the video
   */
  public String watchVideo(String publicId) {
    return cloudinary
        .url()
        .resourceType(ConstantCloudinaryVideo.RESOURCE_VIDEO)
        .publicId(publicId)
        .format(ConstantCloudinaryVideo.FORMAT_MP4)
        .secure(true)
        .generate();
  }

  /**
   * Deletes multiple videos in bulk from Cloudinary.
   *
   * @param publicIds List of public IDs of the videos to delete
   * @throws BadRequestException if deletion fails
   */
  public void deleteVideos(List<String> publicIds) {
    if (publicIds == null || publicIds.isEmpty()) return;

    try {
      cloudinary
          .api()
          .deleteResources(
              publicIds,
              ObjectUtils.asMap(
                  ConstantCloudinaryVideo.RESOURCE_TYPE, ConstantCloudinaryVideo.RESOURCE_VIDEO));
    } catch (Exception e) {
      throw new BadRequestException(StaticMessage.BULK_VIDEO_FAILED + e.getMessage());
    }
  }
}
