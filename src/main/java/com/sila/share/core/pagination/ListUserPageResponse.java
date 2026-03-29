package com.sila.share.core.pagination;

import com.sila.modules.course.dto.CourseResponse;
import com.sila.modules.profile.dto.res.UserResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ListUserPageResponse {
  private List<UserResponse> contents;
  private int page;
  private int limit;
  private long total;
  private int totalPage;
  private boolean hasNext;
}