package com.abika.expense.adapter.out.persistence;

import com.abika.expense.application.port.out.PageResult;
import com.abika.expense.application.port.out.TransactionRepository;
import com.abika.expense.domain.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class TransactionPersistenceAdapter implements TransactionRepository {

    private static final Sort NEWEST_FIRST = Sort.by(Sort.Order.desc("date"), Sort.Order.desc("id"));

    private final TransactionJpaRepository jpa;

    TransactionPersistenceAdapter(TransactionJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public Transaction save(Transaction t) {
        TransactionEntity entity = new TransactionEntity(t.id(), t.userId(), t.categoryId(), t.amount(), t.date(), t.note());
        return toDomain(jpa.save(entity));
    }

    @Override
    public Optional<Transaction> findByIdAndUser(Long id, Long userId) {
        return jpa.findByIdAndUserId(id, userId).map(TransactionPersistenceAdapter::toDomain);
    }

    @Override
    public void delete(Long id) {
        jpa.deleteById(id);
    }

    @Override
    public PageResult<Transaction> findByUserAndMonth(Long userId, YearMonth month, Long categoryIdOrNull, int page, int size) {
        LocalDate from = month.atDay(1);
        LocalDate to = month.atEndOfMonth();
        PageRequest pageable = PageRequest.of(page, size, NEWEST_FIRST);
        Page<TransactionEntity> result = categoryIdOrNull == null
                ? jpa.findByUserIdAndDateBetween(userId, from, to, pageable)
                : jpa.findByUserIdAndCategoryIdAndDateBetween(userId, categoryIdOrNull, from, to, pageable);
        return new PageResult<>(
                result.getContent().stream().map(TransactionPersistenceAdapter::toDomain).toList(),
                page, size, result.getTotalElements());
    }

    @Override
    public long sumByCategoryAndMonth(Long categoryId, YearMonth month) {
        return jpa.sumByCategoryIdAndDateBetween(categoryId, month.atDay(1), month.atEndOfMonth());
    }

    @Override
    public Map<Long, Long> sumPerCategory(Long userId, YearMonth month) {
        return jpa.sumPerCategory(userId, month.atDay(1), month.atEndOfMonth()).stream()
                .collect(Collectors.toMap(
                        TransactionJpaRepository.CategoryTotal::getCategoryId,
                        TransactionJpaRepository.CategoryTotal::getTotal));
    }

    @Override
    public boolean existsByCategory(Long categoryId) {
        return jpa.existsByCategoryId(categoryId);
    }

    private static Transaction toDomain(TransactionEntity e) {
        return new Transaction(e.getId(), e.getUserId(), e.getCategoryId(), e.getAmount(), e.getDate(), e.getNote());
    }
}
