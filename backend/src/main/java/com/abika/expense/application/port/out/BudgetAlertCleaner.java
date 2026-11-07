package com.abika.expense.application.port.out;

public interface BudgetAlertCleaner {

    void deleteByCategory(Long categoryId);
}
