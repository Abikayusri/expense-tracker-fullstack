package com.abika.expense.adapter.out.persistence;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

interface TransactionJpaRepository extends JpaRepository<TransactionEntity, Long> {

    Optional<TransactionEntity> findByIdAndUserId(Long id, Long userId);

    Page<TransactionEntity> findByUserIdAndDateBetween(Long userId, LocalDate from, LocalDate to, Pageable pageable);

    Page<TransactionEntity> findByUserIdAndCategoryIdAndDateBetween(
            Long userId, Long categoryId, LocalDate from, LocalDate to, Pageable pageable);

    boolean existsByCategoryId(Long categoryId);

    @Query("""
            select coalesce(sum(t.amount), 0) from TransactionEntity t
            where t.categoryId = :categoryId and t.date between :from and :to""")
    long sumByCategoryIdAndDateBetween(@Param("categoryId") Long categoryId,
                                       @Param("from") LocalDate from, @Param("to") LocalDate to);

    @Query("""
            select t.categoryId as categoryId, sum(t.amount) as total from TransactionEntity t
            where t.userId = :userId and t.date between :from and :to
            group by t.categoryId""")
    List<CategoryTotal> sumPerCategory(@Param("userId") Long userId,
                                       @Param("from") LocalDate from, @Param("to") LocalDate to);

    interface CategoryTotal {

        Long getCategoryId();

        Long getTotal();
    }
}
