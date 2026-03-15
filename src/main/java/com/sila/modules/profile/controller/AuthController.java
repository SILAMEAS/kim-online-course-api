package com.sila.modules.profile.controller;

import com.sila.modules.profile.dto.req.LoginRequest;
import com.sila.modules.profile.dto.req.SignUpRequest;
import com.sila.modules.profile.dto.req.UserRequest;
import com.sila.modules.profile.dto.res.LoginResponse;
import com.sila.modules.profile.dto.res.UserResponse;
import com.sila.modules.profile.service.AuthService;
import com.sila.modules.profile.service.UserService;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(
    name = "Authentication & Profile",
    description = "APIs for user authentication and profile management")
@RestController
@RequestMapping("/auths")
public class AuthController {

  private final AuthService authService;
  private final UserService userService;

  public AuthController(AuthService authService, UserService userService) {
    this.authService = authService;
    this.userService = userService;
  }

  /** Sign up a new student account */
  @PostMapping("/sign-up")
  @Operation(
      summary = "Student Sign Up",
      description = "Registers a new account. Only users with STUDENT role can sign up.",
      responses = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Account created successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "403",
            description = "Only student can sign up"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "Invalid request data")
      })
  public ResponseEntity<Map<String,String>> signUp(
      @RequestBody(
              description = "Sign up details for the student account",
              required = true,
              content = @Content(schema = @Schema(implementation = SignUpRequest.class)))
          @Valid
          @org.springframework.web.bind.annotation.RequestBody
          SignUpRequest request) {

    return authService.signUp(request);
  }

  /** Sign in to your account */
  @PostMapping("/sign-in")
  @Operation(
      summary = "Sign In",
      description = "Authenticate a user and return access and refresh tokens.",
      responses = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Login successful",
            content = @Content(schema = @Schema(implementation = LoginResponse.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "401",
            description = "Invalid credentials")
      })
  public ResponseEntity<LoginResponse> signIn(
      @RequestBody(
              description = "User login credentials",
              required = true,
              content = @Content(schema = @Schema(implementation = LoginRequest.class)))
          @org.springframework.web.bind.annotation.RequestBody
          LoginRequest loginReq) {

    return authService.signIn(loginReq);
  }

  /** Refresh access token */
  @PostMapping("/refresh-token")
  @Operation(
      summary = "Refresh Token",
      description = "Exchange a valid refresh token for a new access token.",
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "Token refreshed successfully",
            content = @Content(schema = @Schema(implementation = LoginResponse.class))),
        @ApiResponse(responseCode = "401", description = "Invalid or expired refresh token")
      })
  public ResponseEntity<LoginResponse> refreshToken(
      @RequestBody(
              description = "Pass the refresh token in JSON: {\"refreshToken\":\"<token>\"}",
              required = true,
              content = @Content(schema = @Schema(example = "{\"refreshToken\":\"string\"}")))
          @org.springframework.web.bind.annotation.RequestBody
          Map<String, String> request) {

    String refreshToken = request.get("refreshToken");
    return authService.refreshToken(refreshToken);
  }

  /** Test API (hidden from Swagger) */
  @Hidden
  @GetMapping("/test-api")
  @Operation(description = "Test endpoint to check if backend is running")
  public ResponseEntity<String> getTest() {
    return ResponseEntity.ok("API working");
  }

  /** Get profile of logged-in user */
  @GetMapping("/me")
  @Operation(
      summary = "Get Profile",
      description = "Retrieve the profile information of the currently authenticated user.",
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "Profile retrieved successfully",
            content = @Content(schema = @Schema(implementation = UserResponse.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
      })
  public ResponseEntity<UserResponse> getUserByJwtToken() {
    return ResponseEntity.ok(userService.getProfile());
  }

  /** Update profile of logged-in user */
  @PutMapping(value = "/me", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  @Operation(
          summary = "Update Profile",
          description = "Update the account details of the currently authenticated user."
  )
  public ResponseEntity<UserResponse> updateProfile(
          @ModelAttribute UserRequest userReq) {

      return ResponseEntity.ok(userService.update(userReq));
  }
}
