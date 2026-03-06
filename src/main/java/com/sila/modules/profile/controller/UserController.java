package com.sila.modules.profile.controller;

import com.sila.modules.profile.dto.req.SignUpRequest;
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
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Profile Controller", description = "Operations related to Profile")
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
  private final UserService userService;
  private final AuthService authService;
  private final VideoService videoService;

  @Operation(description = "Retrieve a paginated list of users in your system include you. Only ROLE ADMIN can user it")
  @PreAuthorization({ROLE.ADMIN})
  @GetMapping
  public ResponseEntity<EntityResponseHandler<UserResponse>> listUsers(
      @ModelAttribute PaginationRequest request) {
    return new ResponseEntity<>(userService.list(request), HttpStatus.OK);
  }

  @Operation(description = "Operation to create new User")
  @PreAuthorization({ROLE.ADMIN})
  @PostMapping
  public ResponseEntity<String> createUser(@RequestBody @Valid SignUpRequest request) {
    return authService.signUp(request);
  }

  @Operation(description = "Operation to update user")
  @PreAuthorization({ROLE.ADMIN})
  @PutMapping("{id}")
  public ResponseEntity<String> updateUser(
      @PathVariable Long id, @Valid @RequestBody UpdateUserRequest request) {
    return new ResponseEntity<>(userService.updateUser(id, request), HttpStatus.OK);
  }

  @Operation(description = "Operation to delete user")
  @PreAuthorization({ROLE.ADMIN})
  @DeleteMapping("{id}")
  public ResponseEntity<String> deleteUser(@PathVariable Long id) {
    return new ResponseEntity<>(userService.deleteUser(id), HttpStatus.OK);
  }
}
