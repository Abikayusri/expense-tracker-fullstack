package com.abika.expense.application.port.in;

import com.abika.expense.domain.Category;
import com.abika.expense.domain.CategoryType;

import java.util.List;

public interface CategoryUseCase {

    List<Category> list(Long userId, CategoryType typeOrNull);

    Category create(CreateCategoryCommand command);

    Category update(UpdateCategoryCommand command);

    void delete(Long userId, Long id);
}
