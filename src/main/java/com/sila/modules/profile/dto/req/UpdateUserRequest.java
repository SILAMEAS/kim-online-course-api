package com.sila.modules.profile.dto.req;

import com.sila.share.enums.ROLE;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateUserRequest {

    @NotBlank(message = "frist name is required")
    private String fistName;
    @NotBlank(message = "last name is required")
    private String lastName;

    @NotNull
    private ROLE role;


}
