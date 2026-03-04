package com.sila.modules.video.controller;

import com.sila.modules.video.service.VideoService;
import com.sila.share.annotation.PreAuthorization;
import com.sila.share.enums.ROLE;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/videos")
public class VideoController {
  private final VideoService videoService;

  public VideoController(VideoService videoService) {
    this.videoService = videoService;
  }

  @PostMapping("/upload/{courseId}")
  @PreAuthorization({ROLE.ADMIN})
  public ResponseEntity<?> upload(
      @PathVariable Long courseId, @RequestParam String title, @RequestParam MultipartFile file) {

    videoService.uploadVideo(courseId, title, file);

    return ResponseEntity.ok("Video uploaded successfully");
  }


}
