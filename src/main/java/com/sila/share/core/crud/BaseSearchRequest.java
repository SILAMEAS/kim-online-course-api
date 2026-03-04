package com.sila.share.core.crud;

import org.springframework.data.domain.Sort;

/**
 * The base search request
 *
 * @param <T> refers to the sortable column {@link Enum}
 */
public interface BaseSearchRequest<T extends BaseSortEnum> {

  /**
   * @return nullable of search filter
   */
  String search();

  /**
   * @return number of page
   */
  Integer page();

  /**
   * @return the query limit
   */
  Integer limit();

  /**
   * @return sortable column
   */
  T sortField();

  /**
   * @return the sort order
   */
  Sort.Direction sortDirection();
}
