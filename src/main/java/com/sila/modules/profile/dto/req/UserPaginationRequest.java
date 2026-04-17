package com.sila.modules.profile.dto.req;

import com.sila.share.core.pagination.PaginationRequest;
import com.sila.share.enums.ROLE;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserPaginationRequest extends PaginationRequest {
  private ROLE role;
}
