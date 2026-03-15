package com.sila.modules.video.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class ImageService {

  private final ImageCloudinaryService imageCloudinaryService;

  /** Upload single image */
  @Transactional
  public String uploadImage(MultipartFile file) {
    return imageCloudinaryService.uploadImage(file);
  }

  /** Upload multiple images */
  @Transactional
  public List<String> uploadMultipleImages(List<MultipartFile> files) {
    return files.stream().map(imageCloudinaryService::uploadImage).toList();
  }

  /** Delete single image */
  @Transactional
  public void deleteImage(String publicId) {
    imageCloudinaryService.deleteImage(publicId);
  }

  /** Delete multiple images */
  @Transactional
  public void deleteMultipleImages(List<String> publicIds) {
    imageCloudinaryService.deleteImages(publicIds);
  }

  /** Update image */
  @Transactional
  public String updateImage(String oldPublicId, MultipartFile file) {

    return imageCloudinaryService.updateImage(oldPublicId, file);
  }
}
