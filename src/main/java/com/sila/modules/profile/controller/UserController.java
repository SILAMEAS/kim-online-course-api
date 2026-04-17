package com.sila.modules.profile.controller;

import com.sila.modules.enrolment.service.EnrollmentService;
import com.sila.modules.profile.dto.req.CreateUserRequest;
import com.sila.modules.profile.dto.req.UpdateUserRequest;
import com.sila.modules.profile.dto.req.UserPaginationRequest;
import com.sila.modules.profile.dto.res.ListUserPageResponse;
import com.sila.modules.profile.dto.res.UserResponse;
import com.sila.modules.profile.service.AuthService;
import com.sila.modules.profile.service.UserService;
import com.sila.share.annotation.PreAuthorization;
import com.sila.share.core.pagination.PaginationRequest;
import com.sila.share.core.pagination.ResponsePaginationHandler;
import com.sila.share.dto.GeneralResponse;
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
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "User Management", description = "Operations related to managing user accounts")
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

  private final UserService userService;
  private final AuthService authService;
  private final EnrollmentService enrollmentService;

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
            content = @Content(schema = @Schema(implementation = ListUserPageResponse.class))),
        @ApiResponse(responseCode = "403", description = "Access denied")
      })
  public ResponseEntity<ResponsePaginationHandler<UserResponse>> listUsers(
      @ParameterObject UserPaginationRequest request) {
    return ResponseEntity.ok(userService.list(request));
  }

  @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
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
      @Valid @ModelAttribute CreateUserRequest request) {
    return authService.createUser(request);
  }

  @PutMapping(value = "{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
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
  public ResponseEntity<GeneralResponse> updateUser(
      @Parameter(description = "ID of the user to update", example = "1", required = true)
          @PathVariable
          Long id,
      @Valid @ModelAttribute UpdateUserRequest request) {
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

  @GetMapping("/teachers")
  public ResponseEntity<ResponsePaginationHandler<UserResponse>> listTeachers(
      @ParameterObject PaginationRequest request) {
    return ResponseEntity.ok(userService.listTeachers(request));
  }

  @GetMapping("/courses/{courseId}/users")
  @PreAuthorization({ROLE.ADMIN, ROLE.INSTRUCTOR})
  @Operation(
      summary = "List all users",
      description =
          "Retrieve a paginated list of all users including yourself. Only ADMIN can access this endpoint.",
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "Users retrieved successfully",
            content = @Content(schema = @Schema(implementation = ListUserPageResponse.class))),
        @ApiResponse(responseCode = "403", description = "Access denied")
      })
  public ResponseEntity<ResponsePaginationHandler<UserResponse>> listStudentInCourse(
      @PathVariable Long courseId, @ParameterObject UserPaginationRequest request) {
    return ResponseEntity.ok(this.enrollmentService.listUsersByCourse(courseId, request));
  }
}
