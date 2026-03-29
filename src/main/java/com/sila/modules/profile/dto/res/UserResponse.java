package com.sila.modules.profile.dto.res;

import com.sila.modules.profile.model.User;
import com.sila.share.enums.ROLE;
import com.sila.share.enums.UserStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.io.Serializable;
import java.time.Instant;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class UserResponse implements Serializable {
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private Long id;
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private String firstName;
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private String lastName;
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private String email;
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private ROLE role;
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private String imageUrl;
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private UserStatus status;

    public static UserResponseCustom toUserResponseCustom(User user) {
        return UserResponseCustom.builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .role(user.getRole())
                .createdAt(user.getCreatedAt())
                .status(user.getStatus())
                .build();
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class UserResponseCustom {
        private Long id;
        private String firstName;
        private String lastName;
        private String email;
        private ROLE role;
        private int orders;
        private Instant createdAt;
        private UserStatus status;
    }
}

