package com.sila.modules.profile.service;

import com.sila.config.context.UserContext;
import com.sila.config.exception.BadRequestException;
import com.sila.config.exception.NotFoundException;
import com.sila.config.jwt.JwtProvider;
import com.sila.modules.image.model.Image;
import com.sila.modules.image.repository.ImageRepository;
import com.sila.modules.image.service.ImageService;
import com.sila.modules.profile.dto.req.UpdateUserRequest;
import com.sila.modules.profile.dto.req.UserRequest;
import com.sila.modules.profile.dto.res.UserResponse;
import com.sila.modules.profile.model.User;
import com.sila.modules.profile.repository.UserRepository;
import com.sila.modules.profile.spec.UserSpec;
import com.sila.share.Utils;
import com.sila.share.core.crud.AbstractCrudCommon;
import com.sila.share.core.pagination.EntityResponseHandler;
import com.sila.share.core.pagination.PaginationRequest;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

/**
 * Service class for managing user-related operations.
 *
 * <p>This service handles:
 *
 * <ul>
 *   <li>Fetching users by JWT, email, or ID.
 *   <li>Listing users with pagination and search filters.
 *   <li>Updating user information and roles.
 *   <li>Deleting users.
 *   <li>Fetching the current user's profile.
 * </ul>
 */
@Slf4j
@Service
public class UserService extends AbstractCrudCommon<User, Long, UserRepository> {

  private final JwtProvider jwtProvider;
  private final ImageService imageService;
  private final ImageRepository imageRepository;

  protected UserService(
      UserRepository baseRepository,
      ModelMapper mapper,
      JwtProvider jwtProvider,
      ImageService imageService,
      ImageRepository imageRepository) {
    super(baseRepository, mapper);
    this.jwtProvider = jwtProvider;
    this.imageService = imageService;
    this.imageRepository = imageRepository;
  }

  /**
   * Retrieves a user by JWT token.
   *
   * @param jwt JWT token
   * @return User entity corresponding to the JWT
   * @throws NotFoundException if no user is found for the JWT
   */
  public User getByJwt(String jwt) {
    String email = jwtProvider.getEmailFromJwtToken(jwt);
    return getByEmail(email);
  }

  /**
   * Retrieves a user by email.
   *
   * @param email Email of the user
   * @return User entity
   * @throws NotFoundException if no user is found with the email
   */
  public User getByEmail(String email) {
    User foundUser = this.baseRepository.findByEmail(email);
    if (foundUser == null) {
      throw new NotFoundException("User not found");
    }
    return foundUser;
  }

  /**
   * Retrieves a user by ID.
   *
   * @param userId ID of the user
   * @return User entity
   * @throws BadRequestException if the user is not found
   */
  public User getById(Long userId) {
    return this.baseRepository
        .findById(userId)
        .orElseThrow(() -> new BadRequestException("User not found"));
  }

  /**
   * Lists users with pagination and optional search filters.
   *
   * @param request PaginationRequest containing page, limit, and search term
   * @return EntityResponseHandler containing paginated UserResponse DTOs
   */
  public EntityResponseHandler<UserResponse> list(PaginationRequest request) {
    final var spec = UserSpec.search(request.getSearch());
    final var pageable = super.toPageable(request.getPage(), request.getLimit());

    return new EntityResponseHandler<>(
        super.findAll(spec, pageable).map(re -> mapper.map(re, UserResponse.class)));
  }

  /**
   * Updates a user by ID with the given information.
   *
   * @param id ID of the user to update
   * @param request UpdateUserRequest containing new user data
   * @return Success message
   */
  public String updateUser(Long id, UpdateUserRequest request) {
    User user = super.findById(id);
    user.setRole(request.getRole());
    user.setFirstName(request.getFistName());
    user.setLastName(request.getLastName());
    super.save(mapper.map(user, User.class));

    return "User updated successfully";
  }

  /**
   * Deletes a user by ID.
   *
   * @param id ID of the user to delete
   * @return Success message
   * @throws BadRequestException if user does not exist
   */
  public String deleteUser(Long id) {
    super.findById(id);
    super.deleteById(id);
    return "Successfully deleted user";
  }

  /** Updates the current logged-in user's profile. */
  @Transactional
  public UserResponse update(UserRequest userReq) {
    // 1️⃣ Get current logged-in user
    User user = super.findById(UserContext.getUserId());

    // 2️⃣ Update basic info
    updateBasicInfo(userReq, user);

    // 3️⃣ Handle profile image (delete old, upload new, save new publicId)
    String publicId = handleProfileImageUpdate(userReq, user);

    // 4️⃣ Persist user changes
    super.update(user);

    // 5️⃣ Return response with updated info and image URL
    return mapToResponse(user, imageService.getUrlImage(publicId));
  }

  /** Updates user's firstName and lastName if provided */
  private void updateBasicInfo(UserRequest userReq, User user) {
    Utils.setValueSafe(userReq.getFirstName(), user::setFirstName);
    Utils.setValueSafe(userReq.getLastName(), user::setLastName);
  }

  /**
   * Handles profile image update: - deletes old image from Cloudinary - uploads new image - updates
   * Image entity in DB
   */
  private String handleProfileImageUpdate(UserRequest userReq, User user) {
    // Get existing image entity
    Image existingImage = imageService.getImageByUserLogin();
    // If no new file, return existing image publicId
    if (userReq.getFile() == null || userReq.getFile().isEmpty()) {
      return existingImage != null ? existingImage.getPublicId() : null;
    }

    if (existingImage == null) {
      // No existing image, create a new one
      return createNewProfileImage(userReq, user);
    }

    // Update image in Cloudinary (old one will be deleted inside updateImage)
    String newPublicId = imageService.updateImage(existingImage.getPublicId(), userReq.getFile());

    // Persist new publicId in DB
    existingImage.setPublicId(newPublicId);
    imageService.updateImageEntity(existingImage);

    return newPublicId;
  }

  /** Creates a new profile image record in DB and uploads it */
  private String createNewProfileImage(UserRequest userReq, User user) {
    // Upload image to Cloudinary
    String publicId = imageService.uploadImageFolderProfile(userReq.getFile());

    // Create DB record
    Image image = new Image();
    image.setUser(user);
    image.setPublicId(publicId);
    image.setTitle(String.valueOf(user.getId()));
    this.imageRepository.save(image);

    return publicId;
  }

  private UserResponse mapToResponse(User user, String publicId) {
    return UserResponse.builder()
        .id(user.getId())
        .firstName(user.getFirstName())
        .lastName(user.getLastName())
        .role(user.getRole())
        .email(user.getEmail())
        .image_url(publicId)
        .build();
  }

  /**
   * Retrieves the profile of the currently logged-in user.
   *
   * @return UserResponse DTO containing profile information
   */
  @Transactional
  public UserResponse getProfile() {
    return mapToResponse(
        super.findById(UserContext.getUserId()),
        this.imageService.getImageByUserLogin().getPublicId());
  }
}
