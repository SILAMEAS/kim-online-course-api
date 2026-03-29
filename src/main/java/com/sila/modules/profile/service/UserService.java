package com.sila.modules.profile.service;

import com.sila.config.context.UserContext;
import com.sila.config.exception.BadRequestException;
import com.sila.config.exception.NotFoundException;
import com.sila.config.jwt.JwtProvider;
import com.sila.modules.image.Enum.CloudinaryFolder;
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
import com.sila.share.dto.GeneralResponse;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
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

    protected UserService(
            UserRepository baseRepository,
            ModelMapper mapper,
            JwtProvider jwtProvider,
            ImageService imageService) {
        super(baseRepository, mapper);
        this.jwtProvider = jwtProvider;
        this.imageService = imageService;
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

    public EntityResponseHandler<UserResponse> listTeachers(PaginationRequest request) {
        var spec = UserSpec.search(request.getSearch());
        spec = spec.and(UserSpec.byTeacher());
        final var pageable = super.toPageable(request.getPage(), request.getLimit());

        return new EntityResponseHandler<>(
                super.findAll(spec, pageable).map(re -> mapper.map(re, UserResponse.class)));
    }


    /**
     * Updates a user by ID with the given information.
     *
     * @param id      ID of the user to update
     * @param request UpdateUserRequest containing new user data
     * @return Success message
     */
    public GeneralResponse updateUser(Long id, UpdateUserRequest request) {
        User user = super.findById(id);
        Utils.setValueSafe(request.getRole(), user::setRole);
        Utils.setValueSafe(request.getFirstName(), user::setFirstName);
        Utils.setValueSafe(request.getLastName(), user::setLastName);
        Utils.setValueSafe(request.getStatus(), user::setStatus);
        super.save(mapper.map(user, User.class));
        return GeneralResponse.builder().status(HttpStatus.OK.value()).message("User updated successfully").build();
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
     * Updates the current logged-in user's profile.
     */
    @Transactional
    public UserResponse update(UserRequest userReq) {
        // 1️⃣ Get current logged-in user
        var user = super.findById(UserContext.getUserId());
        Utils.setValueSafe(userReq.getFirstName(), user::setFirstName);
        Utils.setValueSafe(userReq.getLastName(), user::setLastName);

        var oldImage = user.getImage();
        var newImage = oldImage;

        if (newImage == null) {
            newImage = this.imageService.createImage(userReq.getFile(), CloudinaryFolder.PROFILE);
        } else {
            newImage =
                    this.imageService.updateImage(oldImage, userReq.getFile(), CloudinaryFolder.PROFILE);
        }

        user.setImage(newImage);

        super.update(user);

        return mapToResponse(user, imageService.getUrlImage(newImage.getPublicId()));
    }

    private UserResponse mapToResponse(User user, String publicId) {
        return UserResponse.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .role(user.getRole())
                .email(user.getEmail())
                .imageUrl(publicId)
                .build();
    }

    /**
     * Retrieves the profile of the currently logged-in user.
     *
     * @return UserResponse DTO containing profile information
     */
    @Transactional
    public UserResponse getProfile() {
        var profileImage = this.findById(UserContext.getUserId()).getImage();
        String publicId = null;
        if (profileImage != null) {
            publicId = this.imageService.getUrlImage(profileImage.getPublicId());
        }
        return mapToResponse(super.findById(UserContext.getUserId()), publicId);
    }
}
