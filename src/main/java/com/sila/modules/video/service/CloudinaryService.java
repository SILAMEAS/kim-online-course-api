package com.sila.modules.video.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.sila.config.exception.BadRequestException;
import com.sila.share.core.pagination.CloudinaryConstant;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class CloudinaryService {

  private final Cloudinary cloudinary;

  public String uploadVideo(MultipartFile file) {
    try {
      final var uploadResult =
          cloudinary
              .uploader()
              .upload(
                  file.getBytes(),
                  ObjectUtils.asMap(
                      CloudinaryConstant.RESOURCE_TYPE_KEY,
                      CloudinaryConstant.RESOURCE_TYPE_VALUE));
      return uploadResult.get(CloudinaryConstant.PUBLIC_ID).toString();
    } catch (IOException e) {
      throw new BadRequestException("Video upload failed");
    }
  }

  public String generateSignedUrl(String publicId) {
    return cloudinary
        .url()
        .resourceType(CloudinaryConstant.RESOURCE_TYPE_VALUE)
        .publicId(publicId)
        .signed(true)
        .generate();
  }

  public void deleteVideo(String publicId) {
    try {
      cloudinary
          .uploader()
          .destroy(
              publicId,
              ObjectUtils.asMap(
                  CloudinaryConstant.RESOURCE_TYPE_KEY, CloudinaryConstant.RESOURCE_TYPE_VALUE));
    } catch (IOException e) {
      throw new BadRequestException("Video delete failed");
    }
  }

  public String updateVideo(String oldPublicId, MultipartFile newFile) {

    if (oldPublicId != null) {
      deleteVideo(oldPublicId);
    }

    return uploadVideo(newFile);
  }

  public String watchVideo(String publicId) {
    return cloudinary
        .url()
        .resourceType(CloudinaryConstant.RESOURCE_TYPE_VALUE)
        .publicId(publicId)
        .format(CloudinaryConstant.FORMAT)
        .secure(true)
        .generate();
  }

  public void deleteVideos(List<String> publicIds) {
    if (publicIds == null || publicIds.isEmpty()) {
      return;
    }

    try {
      cloudinary.api().deleteResources(publicIds, ObjectUtils.asMap("resource_type", "video"));
    } catch (Exception e) {
      throw new BadRequestException("Bulk video delete failed");
    }
  }
}
