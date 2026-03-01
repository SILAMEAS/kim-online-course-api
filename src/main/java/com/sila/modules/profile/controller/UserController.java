package com.sila.modules.profile.controller;


import com.sila.modules.profile.dto.req.SignUpRequest;
import com.sila.modules.profile.dto.req.UpdateUserRequest;
import com.sila.modules.profile.dto.req.UserRequest;
import com.sila.modules.profile.dto.res.UserResponse;
import com.sila.modules.profile.service.AuthService;
import com.sila.modules.profile.service.UserService;
import com.sila.share.annotation.PreAuthorization;
import com.sila.share.dto.req.PaginationRequest;
import com.sila.share.enums.ROLE;
import com.sila.share.pagination.EntityResponseHandler;
import com.sila.share.pagination.PageableUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
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

    @GetMapping("/profile")
    public ResponseEntity<UserResponse> getUserByJwtToken() throws Exception {
        return new ResponseEntity<>(userService.getProfile(), HttpStatus.OK);
    }

    @PutMapping("/profile")
    public ResponseEntity<UserResponse> updateProfile(@ModelAttribute @Valid UserRequest userReq) throws Exception {
        return new ResponseEntity<>(userService.update(userReq), HttpStatus.OK);
    }

    @PreAuthorization({ROLE.ADMIN})
    @GetMapping
    public ResponseEntity<EntityResponseHandler<UserResponse>> listUsers(
            @ModelAttribute PaginationRequest request
    ) throws Exception {
        Pageable pageable = PageableUtil.fromRequest(request);
        return new ResponseEntity<>(userService.list(pageable, request.getSearch()), HttpStatus.OK);
    }

    @PreAuthorization({ROLE.ADMIN})
    @PostMapping
    public ResponseEntity<String> createUser(
            @RequestBody @Valid SignUpRequest request
    ) {
        return authService.signUp(request);
    }


    @PreAuthorization({ROLE.ADMIN})
    @PutMapping("{Id}")
    public ResponseEntity<String> updateUser(@PathVariable Long Id, @Valid @RequestBody UpdateUserRequest request) {
        return new ResponseEntity<>(userService.updateUser(Id, request), HttpStatus.OK);
    }

    @PreAuthorization({ROLE.ADMIN})
    @DeleteMapping("{Id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long Id) {
        return new ResponseEntity<>(userService.deleteUser(Id), HttpStatus.OK);
    }
}
