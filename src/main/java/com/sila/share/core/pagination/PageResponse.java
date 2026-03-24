package com.sila.share.core.pagination;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PageResponse<T> {
    private List<T> contents;
    private int page;
    private int limit;
    private long total;
    private int totalPage;
    private boolean hasNext;
}