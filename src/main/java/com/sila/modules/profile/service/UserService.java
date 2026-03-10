package com.sila.modules.profile.service;

import com.sila.config.context.UserContext;
import com.sila.config.exception.BadRequestException;
import com.sila.config.exception.NotFoundException;
import com.sila.config.jwt.JwtProvider;
import com.sila.modules.profile.spec.UserSpec;
import com.sila.modules.profile.dto.req.UpdateUserRequest;
import com.sila.modules.profile.dto.req.UserRequest;
import com.sila.modules.profile.dto.res.UserResponse;
import com.sila.modules.profile.model.User;
import com.sila.modules.profile.repository.UserRepository;
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

  protected UserService(
      UserRepository baseRepository, ModelMapper mapper, JwtProvider jwtProvider) {
    super(baseRepository, mapper);
    this.jwtProvider = jwtProvider;
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
   * @param Id ID of the user to update
   * @param request UpdateUserRequest containing new user data
   * @return Success message
   */
  public String updateUser(Long Id, UpdateUserRequest request) {
    User user = super.findById(Id);
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

  /**
   * Updates the current logged-in user’s information.
   *
   * <p>Only non-null values in UserRequest are applied.
   *
   * @param userReq UserRequest containing new firstName and lastName
   * @return UserResponse DTO with updated user information
   */
  public UserResponse update(UserRequest userReq) {
    var user = super.findById(UserContext.getUserId());
    Utils.setValueSafe(userReq.getFirstName(), user::setFirstName);
    Utils.setValueSafe(userReq.getLastName(), user::setLastName);
    super.update(user);

    return mapper.map(user, UserResponse.class);
  }

  /**
   * Retrieves the profile of the currently logged-in user.
   *
   * @return UserResponse DTO containing profile information
   */
  @Transactional
  public UserResponse getProfile() {
    User user = super.findById(UserContext.getUserId());
    return mapper.map(user, UserResponse.class);
  }

  /**
   * Counts the total number of users in the system.
   *
   * @return Total user count
   */
  public Long count() {
    return this.baseRepository.count();
  }
}
