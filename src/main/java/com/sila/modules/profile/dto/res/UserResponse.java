package com.sila.modules.profile.dto.res;

import com.sila.modules.profile.model.User;
import com.sila.share.enums.ROLE;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class UserResponse implements Serializable {
    private Long id;
    private String profile;
    private String fullName;
    private String email;
    private ROLE role;

    public static UserResponseCustom toUserResponseCustom(User user) {
        return UserResponseCustom.builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstName(user.getFistName())
                .lastName(user.getLastName())
                .role(user.getRole())
                .createdAt(user.getCreatedAt())
                .build();
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class UserResponseCustom {
        private Long id;
        private String profile;
        private String firstName;
        private String lastName;
        private String email;
        private ROLE role;
        private int orders;
        private LocalDateTime createdAt;
    }
}

