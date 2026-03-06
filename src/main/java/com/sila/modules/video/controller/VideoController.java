package com.sila.modules.video.controller;

import com.sila.modules.video.dto.VideoListResponse;
import com.sila.modules.video.service.VideoService;
import com.sila.share.annotation.PreAuthorization;
import com.sila.share.core.pagination.EntityResponseHandler;
import com.sila.share.core.pagination.PaginationRequest;
import com.sila.share.enums.ROLE;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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

  @GetMapping
  @Operation(description = "Retrieve a paginated list of videos")
  public ResponseEntity<EntityResponseHandler<VideoListResponse>> getVideos(
      @ModelAttribute PaginationRequest paginationRequest) {

    return ResponseEntity.ok(videoService.getAllVideos(paginationRequest));
  }

  @PostMapping("/upload/{courseId}")
  @PreAuthorization({ROLE.ADMIN})
  @Operation(description = "Upload videos to course and store in Cloudinary")
  public ResponseEntity<String> upload(
      @PathVariable Long courseId, @RequestParam String title, @RequestParam MultipartFile file) {
    videoService.uploadVideo(courseId, title, file);
    return ResponseEntity.ok("Video uploaded successfully");
  }

  @GetMapping("/by-course-id/{courseId}")
  @Operation(description = "Retrieve a paginated list of videos in course id")
  public ResponseEntity<EntityResponseHandler<VideoListResponse>> getVideosByCourseId(
      @PathVariable Long courseId, @ModelAttribute PaginationRequest paginationRequest) {

    return ResponseEntity.ok(videoService.getVideosInCourse(courseId, paginationRequest));
  }

  @DeleteMapping("/by-course-id/{courseId}")
  @Operation(
      summary = "Delete all video in course",
      description = "Operation to delete all video in course")
  public ResponseEntity<String> deleteVideosByCourseId(@PathVariable Long courseId) {
    videoService.deleteAllVideoInCourse(courseId);
    return ResponseEntity.ok("Delete all video in course success");
  }

  @GetMapping("/watch/{publicId}")
  @Operation(description = "Get link of video to watch from Cloudinary")
  public ResponseEntity<String> watchVideo(@PathVariable String publicId) {
    return ResponseEntity.ok(this.videoService.watchVideo(publicId));
  }

  @DeleteMapping("/{publicId}")
  @Operation(description = "Delete video by publicId from Cloudinary")
  public ResponseEntity<String> deleteVideo(@PathVariable String publicId) {
    this.videoService.deleteVideo(publicId);
    return ResponseEntity.ok("Video was deleted");
  }

  @PutMapping("/{publicId}")
  @Operation(description = "Upload new video instead of old publicId of video")
  public ResponseEntity<String> updateVideo(
      @PathVariable String publicId, @RequestParam MultipartFile file) {
    return ResponseEntity.ok(this.videoService.updateVideo(publicId, file));
  }
}
