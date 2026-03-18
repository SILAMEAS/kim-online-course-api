package com.sila.modules.profile.service;

import com.sila.config.custom.CustomUserDetails;
import com.sila.config.custom.CustomerUserDetailsService;
import com.sila.config.exception.BadRequestException;
import com.sila.config.exception.NotFoundException;
import com.sila.config.jwt.JwtConstant;
import com.sila.config.jwt.JwtProvider;
import com.sila.modules.image.Enum.CloudinaryFolder;
import com.sila.modules.image.service.ImageService;
import com.sila.modules.profile.dto.req.CreateUserRequest;
import com.sila.modules.profile.dto.req.LoginRequest;
import com.sila.modules.profile.dto.req.SignUpRequest;
import com.sila.modules.profile.dto.res.LoginResponse;
import com.sila.modules.profile.model.User;
import com.sila.modules.profile.repository.UserRepository;
import com.sila.share.enums.ROLE;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Implementation of authentication service.
 *
 * <p>Handles:
 *
 * <ul>
 *   <li>User sign-up (registration)
 *   <li>User sign-in (login) with JWT token generation
 *   <li>Refresh token flow
 * </ul>
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthImp implements AuthService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtProvider jwtProvider;
  private final CustomerUserDetailsService customerUserDetailsService;
  private final UserService userService;
  private final ImageService imageService;

  /**
   * Authenticates a user by email and password.
   *
   * @param email User email
   * @param password User password
   * @return Authentication object if credentials are valid
   * @throws NotFoundException if user does not exist or password mismatch
   */
  private Authentication authenticate(String email, String password) {
    UserDetails userDetails = customerUserDetailsService.loadUserByUsername(email);
    if (userDetails == null || !passwordEncoder.matches(password, userDetails.getPassword())) {
      throw new NotFoundException("Invalid email or password");
    }
    return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
  }

  /**
   * Registers a new user.
   *
   * @param request SignUpRequest containing email, password, firstName, lastName, and role
   * @return ResponseEntity with HTTP status CREATED and success message
   * @throws BadRequestException if the email is already registered
   */
  public ResponseEntity<Map<String, String>> signUp(SignUpRequest request) {
    if (userRepository.existsByEmail(request.getEmail())) {
      throw new BadRequestException("Email is already used");
    }

    User newUser = new User();
    newUser.setEmail(request.getEmail());
    newUser.setFirstName(request.getFirstName());
    newUser.setLastName(request.getLastName());
    newUser.setPassword(passwordEncoder.encode(request.getPassword()));
    var profileImage = imageService.createImage(request.getFile(), CloudinaryFolder.PROFILE);
    newUser.setImage(profileImage);

    userRepository.save(newUser);

    // Explicit Map<String, String> type
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(Collections.singletonMap("message", "User registered successfully"));
  }

  /**
   * Logs in a user and generates access and refresh tokens.
   *
   * @param req LoginRequest containing email and password
   * @return ResponseEntity containing LoginResponse with tokens, userId, role, and expiration info
   * @throws NotFoundException if authentication fails
   */
  public ResponseEntity<LoginResponse> signIn(LoginRequest req) {
    Authentication authentication = authenticate(req.getEmail(), req.getPassword());
    SecurityContextHolder.getContext().setAuthentication(authentication);

    String accessToken = jwtProvider.generateToken(authentication);
    String refreshToken = jwtProvider.generateRefreshToken(authentication);

    User user = userService.getByEmail(req.getEmail());
    Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
    String role = authorities.isEmpty() ? null : authorities.iterator().next().getAuthority();

    LoginResponse response =
        LoginResponse.builder()
            .accessToken(accessToken)
            .refreshToken(refreshToken)
            .userId(user.getId())
            .role(ROLE.valueOf(role))
            .message("Login successfully")
            .refreshTokenExpiresIn(
                jwtProvider.getExpirationIn(JwtConstant.REFRESH_TOKEN_EXPIRATION))
            .accessTokenExpiresIn(jwtProvider.getExpirationIn(JwtConstant.ACCESS_TOKEN_EXPIRATION))
            .refreshTokenExpiresAt(jwtProvider.getExpirationTimestamp(refreshToken))
            .accessTokenExpiresAt(jwtProvider.getExpirationTimestamp(accessToken))
            .build();

    return ResponseEntity.ok(response);
  }

  /**
   * Refreshes JWT tokens using a valid refresh token.
   *
   * <p>If the refresh token is valid:
   *
   * <ul>
   *   <li>Generates a new access token and refresh token
   *   <li>Returns LoginResponse with new tokens and expiration info
   * </ul>
   *
   * @param refreshToken Refresh token string
   * @return ResponseEntity with LoginResponse if valid, or UNAUTHORIZED if invalid
   */
  public ResponseEntity<LoginResponse> refreshToken(String refreshToken) {
    if (jwtProvider.validateRefreshToken(refreshToken)) {
      String email = jwtProvider.getEmailFromJwtToken(refreshToken);
      CustomUserDetails userDetails =
          (CustomUserDetails) customerUserDetailsService.loadUserByUsername(email);

      Authentication auth =
          new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

      String newAccessToken = jwtProvider.generateToken(auth);
      String newRefreshToken = jwtProvider.generateRefreshToken(auth);

      LoginResponse response =
          LoginResponse.builder()
              .accessToken(newAccessToken)
              .refreshToken(newRefreshToken)
              .userId(userDetails.user().getId())
              .role(ROLE.valueOf(userDetails.getAuthorities().iterator().next().getAuthority()))
              .refreshTokenExpiresIn(
                  jwtProvider.getExpirationIn(JwtConstant.REFRESH_TOKEN_EXPIRATION))
              .accessTokenExpiresIn(
                  jwtProvider.getExpirationIn(JwtConstant.ACCESS_TOKEN_EXPIRATION))
              .refreshTokenExpiresAt(jwtProvider.getExpirationTimestamp(newRefreshToken))
              .accessTokenExpiresAt(jwtProvider.getExpirationTimestamp(newAccessToken))
              .message("Token refreshed successfully")
              .build();

      return ResponseEntity.ok(response);
    } else {
      log.warn("Invalid refresh token: {}", refreshToken);
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
    }
  }

  @Override
  public ResponseEntity<Map<String, String>> createUser(CreateUserRequest request) {

    if (userRepository.findByEmail(request.getEmail()) != null) {
      throw new BadRequestException("Email is already used");
    }

    User newUser = new User();
    newUser.setEmail(request.getEmail());
    newUser.setFirstName(request.getFirstName());
    newUser.setLastName(request.getLastName());
    newUser.setRole(request.getRole());
    newUser.setPassword(passwordEncoder.encode(request.getPassword()));

    userRepository.save(newUser);

    // Explicit Map<String, String> type
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(Collections.singletonMap("message", "Create user successfully"));
  }
}
