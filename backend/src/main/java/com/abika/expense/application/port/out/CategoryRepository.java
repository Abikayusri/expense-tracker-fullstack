package com.abika.expense.application.port.out;

import com.abika.expense.domain.Category;
import com.abika.expense.domain.CategoryType;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository {

    /** Sorted by name ascending. {@code typeOrNull == null} returns every type. */
    List<Category> findByUser(Long userId, CategoryType typeOrNull);

    Optional<Category> findByIdAndUser(Long id, Long userId);

    /** Name comparison is case-insensitive; {@code excludeIdOrNull} skips the category being edited. */
    boolean existsByUserAndNameAndType(Long userId, String name, CategoryType type, Long excludeIdOrNull);

    Category save(Category category);

    void delete(Long id);
}
