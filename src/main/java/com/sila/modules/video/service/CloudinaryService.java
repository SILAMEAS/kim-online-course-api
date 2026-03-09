package com.sila.modules.video.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.sila.config.exception.BadRequestException;
import com.sila.share.constant.StaticMessage;
import com.sila.share.core.pagination.CloudinaryConstant;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class CloudinaryService {

  private final Cloudinary cloudinary;

  /** Upload video directly using InputStream (safer for large files) */
  public String uploadVideo(MultipartFile file) {
    try (InputStream inputStream = file.getInputStream()) {
      var uploadResult =
          cloudinary
              .uploader()
              .uploadLarge(
                  inputStream,
                  ObjectUtils.asMap(
                      CloudinaryConstant.RESOURCE_TYPE_KEY,
                      CloudinaryConstant.RESOURCE_TYPE_VALUE,
                      CloudinaryConstant.CHUNK_SIZE,
                      CloudinaryConstant.CHUNK_MB // 6MB chunks
                      ));
      return uploadResult.get(CloudinaryConstant.PUBLIC_ID).toString();
    } catch (IOException e) {
      throw new BadRequestException(StaticMessage.VIDEO_UPLOAD_FAILED + e.getMessage());
    }
  }

  /** Generate signed URL */
  public String generateSignedUrl(String publicId) {
    return cloudinary
        .url()
        .resourceType(CloudinaryConstant.RESOURCE_TYPE_VALUE)
        .publicId(publicId)
        .signed(true)
        .generate();
  }

  /** Delete a single video */
  public void deleteVideo(String publicId) {
    try {
      cloudinary
          .uploader()
          .destroy(
              publicId,
              ObjectUtils.asMap(
                  CloudinaryConstant.RESOURCE_TYPE_KEY, CloudinaryConstant.RESOURCE_TYPE_VALUE));
    } catch (IOException e) {
      throw new BadRequestException("Video delete failed: " + e.getMessage());
    }
  }

  /** Update video (delete old, upload new) */
  public String updateVideo(String oldPublicId, MultipartFile newFile) {
    if (oldPublicId != null) {
      deleteVideo(oldPublicId);
    }
    return uploadVideo(newFile);
  }

  /** Get video URL for viewing */
  public String watchVideo(String publicId) {
    return cloudinary
        .url()
        .resourceType("video")
        .publicId(publicId)
        .format(CloudinaryConstant.FORMAT)
        .secure(true)
        .generate();
  }

  /** Delete multiple videos */
  public void deleteVideos(List<String> publicIds) {
    if (publicIds == null || publicIds.isEmpty()) return;

    try {
      cloudinary.api().deleteResources(publicIds, ObjectUtils.asMap("resource_type", "video"));
    } catch (Exception e) {
      throw new BadRequestException("Bulk video delete failed: " + e.getMessage());
    }
  }
}
