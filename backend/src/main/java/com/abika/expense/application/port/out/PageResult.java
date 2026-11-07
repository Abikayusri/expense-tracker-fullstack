package com.abika.expense.application.port.out;

import java.util.List;

public record PageResult<T>(List<T> content, int page, int size, long totalElements) {
}
