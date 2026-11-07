package com.abika.expense.application.service;

import com.abika.expense.application.port.in.SummaryUseCase;
import com.abika.expense.application.port.out.CategoryRepository;
import com.abika.expense.application.port.out.TransactionRepository;
import com.abika.expense.domain.MonthlySummary;
import org.springframework.transaction.annotation.Transactional;

import java.time.YearMonth;

public class SummaryService implements SummaryUseCase {

    private final CategoryRepository categories;
    private final TransactionRepository transactions;

    public SummaryService(CategoryRepository categories, TransactionRepository transactions) {
        this.categories = categories;
        this.transactions = transactions;
    }

    @Override
    @Transactional(readOnly = true)
    public MonthlySummary get(Long userId, YearMonth month) {
        return MonthlySummary.of(categories.findByUser(userId, null), transactions.sumPerCategory(userId, month));
    }
}
