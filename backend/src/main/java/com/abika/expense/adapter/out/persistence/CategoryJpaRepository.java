package com.abika.expense.adapter.out.persistence;

import com.abika.expense.domain.CategoryType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

interface CategoryJpaRepository extends JpaRepository<CategoryEntity, Long> {

    List<CategoryEntity> findByUserIdOrderByNameAsc(Long userId);

    List<CategoryEntity> findByUserIdAndTypeOrderByNameAsc(Long userId, CategoryType type);

    Optional<CategoryEntity> findByIdAndUserId(Long id, Long userId);

    boolean existsByUserIdAndTypeAndNameIgnoreCase(Long userId, CategoryType type, String name);

    boolean existsByUserIdAndTypeAndNameIgnoreCaseAndIdNot(Long userId, CategoryType type, String name, Long id);
}
