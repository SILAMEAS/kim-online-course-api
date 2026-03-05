package com.sila.share.core.pagination;

import java.util.ArrayList;
import java.util.List;
import lombok.*;
import org.springframework.data.domain.Page;

/**
 * Generic wrapper for API responses.
 *
 * @param <T> the type of the response body
 */
@Getter
@Setter
@Builder
@ToString
@EqualsAndHashCode
@NoArgsConstructor
public class EntityResponseHandler<T> {
  private List<T> contents;
  private int page;
  private int limit;
  private int totalPage;
  private long total;
  private boolean hasNext;

  /**
   * Creates a response handler from a page result.
   *
   * @param page the paginated data
   */
  public EntityResponseHandler(Page<T> page) {
    this(
        page.getContent(),
        page.getNumber() + 1,
        page.getSize(),
        page.getTotalPages(),
        page.getTotalElements(),
        page.hasNext());
  }

  /**
   * Creates a response handler with pagination details.
   *
   * @param contents the list of contents
   * @param page the current page number
   * @param pageSize the size of the page
   * @param totalPages the total number of pages
   * @param total the total number of elements
   * @param hasNext indicates if there is a next page
   */
  public EntityResponseHandler(
      List<T> contents, int page, int pageSize, int totalPages, long total, boolean hasNext) {
    this.contents = new ArrayList<>(contents);
    this.page = page;
    this.totalPage = totalPages;
    this.limit = pageSize;
    this.total = total;
    this.hasNext = hasNext;
  }

  /**
   * Creates a response handler with basic pagination details.
   *
   * @param contents the list of contents
   * @param page the current page number
   * @param pageSize the size of the page
   * @param total the total number of elements
   */
  public EntityResponseHandler(List<T> contents, int page, int pageSize, long total) {
    this.contents = new ArrayList<>(contents);
    this.page = page + 1;
    this.total = total;
    this.limit = pageSize;
    this.totalPage = pageSize == 0 ? 1 : (int) Math.ceil((double) total / (double) pageSize);
    this.hasNext = page + 1 < totalPage;
  }
}
