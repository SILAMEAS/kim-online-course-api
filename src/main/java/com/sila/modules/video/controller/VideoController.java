package com.sila.modules.video.controller;

import com.sila.modules.video.dto.UpdateVideoRequest;
import com.sila.modules.video.dto.UploadVideoRequest;
import com.sila.modules.video.dto.VideoListResponse;
import com.sila.modules.video.service.VideoService;
import com.sila.share.annotation.PreAuthorization;
import com.sila.share.core.pagination.PaginationRequest;
import com.sila.share.core.pagination.ResponsePaginationHandler;
import com.sila.share.dto.GeneralResponse;
import com.sila.share.enums.ROLE;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Video Management", description = "APIs for managing course videos")
@RestController
@RequestMapping("/api/videos")
public class VideoController {

  private final VideoService videoService;

  public VideoController(VideoService videoService) {
    this.videoService = videoService;
  }

  @GetMapping
  @Operation(
      summary = "List all videos",
      description = "Retrieve a paginated list of all videos across courses.",
      responses = {
        @ApiResponse(responseCode = "200", description = "Videos retrieved successfully")
      })
  public ResponseEntity<ResponsePaginationHandler<VideoListResponse>> getVideos(
      @ParameterObject PaginationRequest paginationRequest) {
    return ResponseEntity.ok(videoService.getAllVideos(paginationRequest));
  }

  @PostMapping(value = "/upload/{courseId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  @PreAuthorization({ROLE.ADMIN})
  @Operation(
      summary = "Upload video",
      description =
          "Upload a new video to a specific course. Only ADMIN can perform this operation.",
      responses = {
        @ApiResponse(responseCode = "200", description = "Video uploaded successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied")
      })
  public ResponseEntity<GeneralResponse> uploadVideo(
      @PathVariable Long courseId, @Valid @ModelAttribute UploadVideoRequest request) {
    videoService.uploadVideo(courseId, request.getTitle(), request.getFile());
    return ResponseEntity.ok(
        GeneralResponse.builder().status(200).message("Video uploaded successfully").build());
  }

  @GetMapping("/by-course-id/{courseId}")
  @Operation(
      summary = "List videos by course",
      description = "Retrieve a paginated list of videos for a specific course.",
      responses = {
        @ApiResponse(responseCode = "200", description = "Videos retrieved successfully")
      })
  public ResponseEntity<ResponsePaginationHandler<VideoListResponse>> getVideosByCourseId(
      @Parameter(description = "Course ID", example = "1") @PathVariable Long courseId,
      @ParameterObject PaginationRequest paginationRequest) {
    return ResponseEntity.ok(videoService.getVideosInCourse(courseId, paginationRequest));
  }

  @DeleteMapping("/by-course-id/{courseId}")
  @PreAuthorization({ROLE.ADMIN})
  @Operation(
      summary = "Delete all videos in a course",
      description =
          "Delete all videos associated with a specific course. Only ADMIN can perform this operation.",
      responses = {
        @ApiResponse(responseCode = "200", description = "All videos deleted successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied")
      })
  public ResponseEntity<String> deleteVideosByCourseId(
      @Parameter(description = "Course ID", example = "1") @PathVariable Long courseId) {
    videoService.deleteAllVideoInCourse(courseId);
    return ResponseEntity.ok("All videos in the course deleted successfully");
  }

  @GetMapping("/watch/{publicId}")
  @Operation(
      summary = "Watch video",
      description = "Get the streaming link of a video by its Cloudinary public ID.",
      responses = {
        @ApiResponse(responseCode = "200", description = "Video link retrieved successfully")
      })
  public ResponseEntity<String> watchVideo(
      @Parameter(description = "Public ID of the video in Cloudinary") @PathVariable
          String publicId) {
    return ResponseEntity.ok(videoService.watchVideo(publicId));
  }

  @DeleteMapping("/{id}/publicId/{publicId}")
  @PreAuthorization({ROLE.ADMIN})
  @Operation(
      summary = "Delete video",
      description =
          "Delete a video from Cloudinary by its public ID. Only ADMIN can perform this operation.",
      responses = {
        @ApiResponse(responseCode = "200", description = "Video deleted successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied")
      })
  public ResponseEntity<GeneralResponse> deleteVideo(
      @Parameter(description = "Public ID of the video in Cloudinary") @PathVariable
          String publicId,
      @Parameter(description = "Video ID") @PathVariable Long id) {
    videoService.deleteVideo(publicId, id);
    return ResponseEntity.ok(
        GeneralResponse.builder().status(200).message("Video deleted successfully").build());
  }

  @PutMapping("/{id}")
  @PreAuthorization({ROLE.ADMIN})
  @Operation(
      summary = "Update video",
      description =
          "Replace an existing video by its public ID. Only ADMIN can perform this operation.",
      responses = {
        @ApiResponse(responseCode = "200", description = "Video updated successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied")
      })
  public ResponseEntity<GeneralResponse> updateVideo(
      @PathVariable Long id, @Valid @ModelAttribute UpdateVideoRequest request) {
    return ResponseEntity.ok(
        GeneralResponse.builder()
            .message(videoService.updateVideo(request, id))
            .status(200)
            .build());
  }
}
