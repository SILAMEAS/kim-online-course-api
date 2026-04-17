package com.sila.modules.profile.dto.res;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ListWishlistPageResponse {
  private List<WishlistResponse> contents;
  private int page;
  private int limit;
  private long total;
  private int totalPage;
  private boolean hasNext;
}