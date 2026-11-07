package com.abika.expense.adapter.out.persistence;

import com.abika.expense.application.port.out.CategoryRepository;
import com.abika.expense.domain.Category;
import com.abika.expense.domain.CategoryType;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class CategoryPersistenceAdapter implements CategoryRepository {

    private final CategoryJpaRepository jpa;

    CategoryPersistenceAdapter(CategoryJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public List<Category> findByUser(Long userId, CategoryType typeOrNull) {
        List<CategoryEntity> entities = typeOrNull == null
                ? jpa.findByUserIdOrderByNameAsc(userId)
                : jpa.findByUserIdAndTypeOrderByNameAsc(userId, typeOrNull);
        return entities.stream().map(CategoryPersistenceAdapter::toDomain).toList();
    }

    @Override
    public Optional<Category> findByIdAndUser(Long id, Long userId) {
        return jpa.findByIdAndUserId(id, userId).map(CategoryPersistenceAdapter::toDomain);
    }

    @Override
    public boolean existsByUserAndNameAndType(Long userId, String name, CategoryType type, Long excludeIdOrNull) {
        return excludeIdOrNull == null
                ? jpa.existsByUserIdAndTypeAndNameIgnoreCase(userId, type, name)
                : jpa.existsByUserIdAndTypeAndNameIgnoreCaseAndIdNot(userId, type, name, excludeIdOrNull);
    }

    @Override
    public Category save(Category category) {
        CategoryEntity entity = new CategoryEntity(
                category.id(), category.userId(), category.name(), category.type(), category.monthlyBudget());
        return toDomain(jpa.save(entity));
    }

    @Override
    public void delete(Long id) {
        jpa.deleteById(id);
    }

    private static Category toDomain(CategoryEntity e) {
        return new Category(e.getId(), e.getUserId(), e.getName(), e.getType(), e.getMonthlyBudget());
    }
}
