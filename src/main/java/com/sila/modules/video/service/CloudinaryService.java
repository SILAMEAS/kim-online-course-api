package com.sila.modules.video.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.sila.config.exception.BadRequestException;
import java.io.IOException;
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
              .upload(file.getBytes(), ObjectUtils.asMap("resource_type", "video"));
      return uploadResult.get("public_id").toString();
    } catch (IOException e) {
      throw new BadRequestException("Video upload failed");
    }
  }

  public String generateSignedUrl(String publicId) {
    return cloudinary.url().resourceType("video").publicId(publicId).signed(true).generate();
  }
}
