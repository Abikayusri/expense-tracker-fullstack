package com.abika.expense.application.fake;

import com.abika.expense.application.port.out.CategoryRepository;
import com.abika.expense.domain.Category;
import com.abika.expense.domain.CategoryType;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class InMemoryCategoryRepository implements CategoryRepository {

    private final List<Category> categories = new ArrayList<>();
    private long nextId = 1;

    @Override
    public List<Category> findByUser(Long userId, CategoryType typeOrNull) {
        return categories.stream()
                .filter(c -> c.userId().equals(userId))
                .filter(c -> typeOrNull == null || c.type() == typeOrNull)
                .sorted(Comparator.comparing(Category::name))
                .toList();
    }

    @Override
    public Optional<Category> findByIdAndUser(Long id, Long userId) {
        return categories.stream().filter(c -> c.id().equals(id) && c.userId().equals(userId)).findFirst();
    }

    @Override
    public boolean existsByUserAndNameAndType(Long userId, String name, CategoryType type, Long excludeIdOrNull) {
        return categories.stream().anyMatch(c -> c.userId().equals(userId)
                && c.type() == type
                && c.name().equalsIgnoreCase(name)
                && !Objects.equals(c.id(), excludeIdOrNull));
    }

    @Override
    public Category save(Category category) {
        Category saved = category.id() == null
                ? new Category(nextId++, category.userId(), category.name(), category.type(), category.monthlyBudget())
                : category;
        categories.removeIf(c -> c.id().equals(saved.id()));
        categories.add(saved);
        return saved;
    }

    @Override
    public void delete(Long id) {
        categories.removeIf(c -> c.id().equals(id));
    }

    public List<Category> all() {
        return List.copyOf(categories);
    }
}
