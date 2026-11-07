package com.abika.expense.application.port.out;

public interface TransactionUsageChecker {

    boolean existsByCategory(Long categoryId);
}
