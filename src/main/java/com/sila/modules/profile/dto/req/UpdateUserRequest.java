package com.sila.modules.profile.dto.req;

import com.sila.share.enums.ROLE;
import com.sila.share.enums.UserStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateUserRequest {

    @NotBlank(message = "first name is required")
    private String firstName;
    @NotBlank(message = "last name is required")
    private String lastName;
    private ROLE role;
    private UserStatus status;


}
