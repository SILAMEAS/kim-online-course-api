package com.sila.modules.image.controller;

import com.sila.modules.image.Enum.CloudinaryFolder;
import com.sila.modules.image.service.ImageService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/images")
public class ImageController {
  private final ImageService imageService;

  public ImageController(ImageService imageService) {
    this.imageService = imageService;
  }

  @DeleteMapping("/{publicId}")
  ResponseEntity<String> deleteImage(@PathVariable String publicId) {
    imageService.deleteImageByPublicId(CloudinaryFolder.PROFILE.getValue() + "/" + publicId);
    return new ResponseEntity<>("Delete success", HttpStatus.OK);
  }
}
