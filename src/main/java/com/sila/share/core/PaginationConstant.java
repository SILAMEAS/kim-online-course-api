package com.sila.share.core;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class PaginationConstant {

    public static final Integer DEFAULT_PAGE_SIZE = 10;
    public static final Integer DEFAULT_PAGE_NUMBER = 1;
    public static final String DEFAULT_SORT_BY_FIELD = "id";
    public static final String DEFAULT_SEARCH = "";
}
