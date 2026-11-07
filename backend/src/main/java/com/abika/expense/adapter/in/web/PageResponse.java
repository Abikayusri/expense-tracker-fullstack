package com.abika.expense.adapter.in.web;

import com.abika.expense.application.port.out.PageResult;

import java.util.List;
import java.util.function.Function;

public record PageResponse<T>(List<T> content, int page, int size, long totalElements) {

    static <D, T> PageResponse<T> from(PageResult<D> result, Function<D, T> mapper) {
        return new PageResponse<>(result.content().stream().map(mapper).toList(),
                result.page(), result.size(), result.totalElements());
    }
}
