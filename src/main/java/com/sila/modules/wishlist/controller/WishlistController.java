package com.sila.modules.wishlist.controller;

import com.sila.modules.wishlist.dto.ListWishlistPageResponse;
import com.sila.modules.wishlist.dto.WishlistResponse;
import com.sila.modules.wishlist.service.WishlistService;
import com.sila.share.core.pagination.PaginationRequest;
import com.sila.share.core.pagination.ResponsePaginationHandler;
import com.sila.share.dto.GeneralResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/wishlists")
@RequiredArgsConstructor
public class WishlistController {

  private final WishlistService wishlistService;

  /** Add course to wishlist */
  @PostMapping
  public ResponseEntity<GeneralResponse> addToWishlist(@RequestParam Long courseId) {
    wishlistService.addToWishlist(courseId);
    return ResponseEntity.ok(
        GeneralResponse.builder().status(200).message("Add to wishlist successfully").build());
  }

  /** Remove course from wishlist */
  @DeleteMapping
  public ResponseEntity<Void> removeFromWishlist(@RequestParam Long courseId) {
    wishlistService.removeFromWishlist(courseId);
    return ResponseEntity.noContent().build();
  }

  /** Get all wishlist items for a user */
  @GetMapping("/{userId}")
  @Operation(
      summary = "List all wishlist",
      description =
          "Retrieve a paginated list of all users including yourself. Only ADMIN can access this endpoint.",
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "Users retrieved successfully",
            content = @Content(schema = @Schema(implementation = ListWishlistPageResponse.class))),
        @ApiResponse(responseCode = "403", description = "Access denied")
      })
  public ResponseEntity<ResponsePaginationHandler<WishlistResponse>> getUserWishlist(
      @PathVariable Long userId, @ParameterObject @Validated PaginationRequest request) {
    return ResponseEntity.ok(wishlistService.getUserWishlist(request, userId));
  }

  /** Check if course is in wishlist */
  @GetMapping("/exists")
  public ResponseEntity<Boolean> exists(@RequestParam Long userId, @RequestParam Long courseId) {
    return ResponseEntity.ok(wishlistService.exists(userId, courseId));
  }
}
