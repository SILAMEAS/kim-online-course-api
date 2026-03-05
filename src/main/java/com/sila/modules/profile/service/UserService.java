package com.sila.modules.profile.service;

import com.sila.config.context.UserContext;
import com.sila.config.exception.BadRequestException;
import com.sila.config.exception.NotFoundException;
import com.sila.config.jwt.JwtProvider;
import com.sila.modules.profile.UserSpec;
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

@Slf4j
@Service
public class UserService extends AbstractCrudCommon<User, Long, UserRepository> {
    private final JwtProvider jwtProvider;

    protected UserService(
            UserRepository baseRepository, ModelMapper mapper, JwtProvider jwtProvider) {
        super(baseRepository, mapper);
        this.jwtProvider = jwtProvider;
    }

    public User getByJwt(String jwt) {
        String email = jwtProvider.getEmailFromJwtToken(jwt);
        return getByEmail(email);
    }

    public User getByEmail(String email) {
        User foundUser = this.baseRepository.findByEmail(email);
        if (foundUser == null) {
            throw new NotFoundException("User not found");
        }
        return foundUser;
    }

    public User getById(Long userId) {
        return this.baseRepository
                .findById(userId)
                .orElseThrow(() -> new BadRequestException("User not found"));
    }

    public EntityResponseHandler<UserResponse> list(PaginationRequest request) {
        final var spec = UserSpec.search(request.getSearch());
        final var pageable = super.toPageable(request.getPage(), request.getLimit());

        return new EntityResponseHandler<>(
                super.findAll(spec, pageable).map(re -> mapper.map(re, UserResponse.class)));
    }


    public String updateUser(Long Id, UpdateUserRequest request) {
        User user = super.findById(Id);
        user.setRole(request.getRole());
        user.setFirstName(request.getFistName());
        user.setLastName(request.getLastName());
        super.save(mapper.map(user, User.class));

        return "User updated successfully";
    }

    public String deleteUser(Long id) {
        super.findById(id);
        super.deleteById(id);
        return "Successfully deleted user";
    }

    public UserResponse update(UserRequest userReq) {
        var user = UserContext.getUser();

        Utils.setValueSafe(userReq.getFirstName(), user::setFirstName);
        Utils.setValueSafe(userReq.getLastName(), user::setLastName);

        return mapper.map(super.save(user), UserResponse.class);
    }

    @Transactional
    public UserResponse getProfile() {
        User user = super.findById(UserContext.getUserId());
        return mapper.map(user, UserResponse.class);
    }

    public Long count() {
        return this.baseRepository.count();
    }
}
