package com.abika.expense.application.fake;

import com.abika.expense.application.port.out.PageResult;
import com.abika.expense.application.port.out.TransactionRepository;
import com.abika.expense.domain.Transaction;

import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class InMemoryTransactionRepository implements TransactionRepository {

    private final List<Transaction> transactions = new ArrayList<>();
    private long nextId = 1;

    @Override
    public Transaction save(Transaction t) {
        Transaction saved = t.id() == null
                ? new Transaction(nextId++, t.userId(), t.categoryId(), t.amount(), t.date(), t.note())
                : t;
        transactions.removeIf(existing -> existing.id().equals(saved.id()));
        transactions.add(saved);
        return saved;
    }

    @Override
    public Optional<Transaction> findByIdAndUser(Long id, Long userId) {
        return transactions.stream().filter(t -> t.id().equals(id) && t.userId().equals(userId)).findFirst();
    }

    @Override
    public void delete(Long id) {
        transactions.removeIf(t -> t.id().equals(id));
    }

    @Override
    public PageResult<Transaction> findByUserAndMonth(Long userId, YearMonth month, Long categoryIdOrNull, int page, int size) {
        List<Transaction> matching = transactions.stream()
                .filter(t -> t.userId().equals(userId) && t.month().equals(month))
                .filter(t -> categoryIdOrNull == null || t.categoryId().equals(categoryIdOrNull))
                .sorted(Comparator.comparing(Transaction::date).thenComparing(Transaction::id).reversed())
                .toList();
        List<Transaction> content = matching.stream().skip((long) page * size).limit(size).toList();
        return new PageResult<>(content, page, size, matching.size());
    }

    @Override
    public long sumByCategoryAndMonth(Long categoryId, YearMonth month) {
        return transactions.stream()
                .filter(t -> t.categoryId().equals(categoryId) && t.month().equals(month))
                .mapToLong(Transaction::amount)
                .sum();
    }

    @Override
    public Map<Long, Long> sumPerCategory(Long userId, YearMonth month) {
        return transactions.stream()
                .filter(t -> t.userId().equals(userId) && t.month().equals(month))
                .collect(Collectors.groupingBy(Transaction::categoryId, Collectors.summingLong(Transaction::amount)));
    }

    @Override
    public boolean existsByCategory(Long categoryId) {
        return transactions.stream().anyMatch(t -> t.categoryId().equals(categoryId));
    }

    public List<Transaction> all() {
        return List.copyOf(transactions);
    }
}
