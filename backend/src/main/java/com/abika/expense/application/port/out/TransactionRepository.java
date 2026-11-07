package com.abika.expense.application.port.out;

import com.abika.expense.domain.Transaction;

import java.time.YearMonth;
import java.util.Map;
import java.util.Optional;

public interface TransactionRepository extends TransactionUsageChecker {

    Transaction save(Transaction transaction);

    Optional<Transaction> findByIdAndUser(Long id, Long userId);

    void delete(Long id);

    /** Sorted by date descending, then id descending. {@code categoryIdOrNull == null} returns every category. */
    PageResult<Transaction> findByUserAndMonth(Long userId, YearMonth month, Long categoryIdOrNull, int page, int size);

    long sumByCategoryAndMonth(Long categoryId, YearMonth month);

    /** Category id to total amount; categories without transactions in the month are absent. */
    Map<Long, Long> sumPerCategory(Long userId, YearMonth month);
}
