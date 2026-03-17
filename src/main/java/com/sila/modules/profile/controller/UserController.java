package com.sila.modules.profile.controller;

import com.sila.modules.profile.dto.req.CreateUserRequest;
import com.sila.modules.profile.dto.req.UpdateUserRequest;
import com.sila.modules.profile.dto.res.UserResponse;
import com.sila.modules.profile.service.AuthService;
import com.sila.modules.profile.service.UserService;
import com.sila.modules.video.service.VideoService;
import com.sila.share.annotation.PreAuthorization;
import com.sila.share.core.pagination.EntityResponseHandler;
import com.sila.share.core.pagination.PaginationRequest;
import com.sila.share.enums.ROLE;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "User Management", description = "Operations related to managing user accounts")
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

  private final UserService userService;
  private final AuthService authService;
  private final VideoService videoService;

  @GetMapping
  @PreAuthorization({ROLE.ADMIN})
  @Operation(
      summary = "List all users",
      description =
          "Retrieve a paginated list of all users including yourself. Only ADMIN can access this endpoint.",
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "Users retrieved successfully",
            content = @Content(schema = @Schema(implementation = UserResponse.class))),
        @ApiResponse(responseCode = "403", description = "Access denied")
      })
  public ResponseEntity<EntityResponseHandler<UserResponse>> listUsers(
      @ParameterObject PaginationRequest request) {
    return ResponseEntity.ok(userService.list(request));
  }

  @PostMapping
  @PreAuthorization({ROLE.ADMIN})
  @Operation(
      summary = "Create a new user",
      description = "Create a new user account. Only ADMIN can perform this operation.",
      responses = {
        @ApiResponse(responseCode = "200", description = "User created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request data"),
        @ApiResponse(responseCode = "403", description = "Access denied")
      })
  public ResponseEntity<Map<String, String>> createUser(
      @Valid @RequestBody CreateUserRequest request) {
    return authService.createUser(request);
  }

  @PutMapping("{id}")
  @PreAuthorization({ROLE.ADMIN})
  @Operation(
      summary = "Update user",
      description = "Update an existing user's details. Only ADMIN can perform this operation.",
      responses = {
        @ApiResponse(responseCode = "200", description = "User updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid update data"),
        @ApiResponse(responseCode = "403", description = "Access denied"),
        @ApiResponse(responseCode = "404", description = "User not found")
      })
  public ResponseEntity<String> updateUser(
      @Parameter(description = "ID of the user to update", example = "1", required = true)
          @PathVariable
          Long id,
      @Valid @RequestBody UpdateUserRequest request) {
    return ResponseEntity.ok(userService.updateUser(id, request));
  }

  @DeleteMapping("{id}")
  @PreAuthorization({ROLE.ADMIN})
  @Operation(
      summary = "Delete user",
      description = "Delete a user by ID. Only ADMIN can perform this operation.",
      responses = {
        @ApiResponse(responseCode = "200", description = "User deleted successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied"),
        @ApiResponse(responseCode = "404", description = "User not found")
      })
  public ResponseEntity<String> deleteUser(
      @Parameter(description = "ID of the user to delete", example = "1", required = true)
          @PathVariable
          Long id) {
    return ResponseEntity.ok(userService.deleteUser(id));
  }
}
