package com.sila.modules.profile.controller;

import com.sila.config.exception.AccessDeniedException;
import com.sila.modules.profile.dto.req.LoginRequest;
import com.sila.modules.profile.dto.req.SignUpRequest;
import com.sila.modules.profile.dto.req.UserRequest;
import com.sila.modules.profile.dto.res.LoginResponse;
import com.sila.modules.profile.dto.res.UserResponse;
import com.sila.modules.profile.service.AuthService;
import com.sila.modules.profile.service.UserService;
import com.sila.share.enums.ROLE;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Auth Controller", description = "Operations related to Auth")
@RestController
@RequestMapping("/auths")
public class AuthController {
  private final AuthService authService;
  private final UserService userService;

  public AuthController(AuthService authService, UserService userService) {
    this.authService = authService;
    this.userService = userService;
  }

  @PostMapping("/sign-up")
  public ResponseEntity<String> signUp(@RequestBody @Valid SignUpRequest request) {
    if (request.getRole().equals(ROLE.STUDENT)) {
      return authService.signUp(request);
    } else {
      throw new AccessDeniedException("Only student can sign up");
    }
  }

  @PostMapping("/sign-in")
  public ResponseEntity<LoginResponse> signIn(@RequestBody LoginRequest loginReq) {
    return authService.signIn(loginReq);
  }

  @PostMapping("/refresh-token")
  public ResponseEntity<LoginResponse> refreshToken(@RequestBody Map<String, String> request) {
    String refreshToken = request.get("refreshToken");
    return authService.refreshToken(refreshToken);
  }

  @Hidden
  @GetMapping("/test-api")
  public ResponseEntity<String> getTest() {
    return new ResponseEntity<>("Api working", HttpStatus.OK);
  }

  @GetMapping("/me")
  public ResponseEntity<UserResponse> getUserByJwtToken() {
    return new ResponseEntity<>(userService.getProfile(), HttpStatus.OK);
  }

  @PutMapping("/me")
  public ResponseEntity<UserResponse> updateProfile(@ModelAttribute @Valid UserRequest userReq) {
    return new ResponseEntity<>(userService.update(userReq), HttpStatus.OK);
  }
}
