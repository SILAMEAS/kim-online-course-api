package com.sila.modules.video.controller;

import com.sila.modules.video.dto.VideoListResponse;
import com.sila.modules.video.service.VideoService;
import com.sila.share.annotation.PreAuthorization;
import com.sila.share.core.pagiation.PaginationRequest;
import com.sila.share.enums.ROLE;
import com.sila.share.pagination.EntityResponseHandler;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
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
  public ResponseEntity<String> upload(
      @PathVariable Long courseId, @RequestParam String title, @RequestParam MultipartFile file) {
    videoService.uploadVideo(courseId, title, file);
    return ResponseEntity.ok("Video uploaded successfully");
  }

  @GetMapping("/courses/{courseId}")
  public ResponseEntity<EntityResponseHandler<VideoListResponse>> getVideos(
      @PathVariable Long courseId, @ModelAttribute PaginationRequest paginationRequest) {

    return ResponseEntity.ok(videoService.getVideos(courseId, paginationRequest));
  }

  @GetMapping("/watch/{publicId}")
  public ResponseEntity<String> watchVideo(@PathVariable String publicId) {
    return ResponseEntity.ok(this.videoService.watchVideo(publicId));
  }

  @DeleteMapping("/{publicId}")
  public ResponseEntity<String> deleteVideo(@PathVariable String publicId) {
    this.videoService.deleteVideo(publicId);
    return ResponseEntity.ok("Video was deleted");
  }

  @GetMapping("/{publicId}")
  public ResponseEntity<String> updateVideo(
      @PathVariable String publicId, @RequestParam MultipartFile file) {
    return ResponseEntity.ok(this.videoService.updateVideo(publicId, file));
  }
}
