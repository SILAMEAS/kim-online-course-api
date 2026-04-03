package com.sila.modules.image.controller;

import com.sila.modules.image.dto.ImageListResponse;
import com.sila.modules.image.service.ImageService;
import com.sila.share.core.pagination.EntityResponseHandler;
import com.sila.share.core.pagination.ImagesPageResponse;
import com.sila.share.core.pagination.PaginationRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/images")
@Tag(name = "ImageController Management", description = "APIs for managing ImageController content")
public class ImageController {

  private final ImageService imageService;

  public ImageController(ImageService imageService) {
    this.imageService = imageService;
  }

  /** Get all images */
  @GetMapping
  @Operation(
      summary = "Get all courses",
      description = "Retrieve a paginated list of courses. Supports pagination parameters.",
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "Courses retrieved successfully",
            content = @Content(schema = @Schema(implementation = ImagesPageResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid pagination parameters")
      })
  public ResponseEntity<EntityResponseHandler<ImageListResponse>> listImages(
      @ParameterObject PaginationRequest request) {

    return ResponseEntity.ok(imageService.listAllImage(request));
  }
}
