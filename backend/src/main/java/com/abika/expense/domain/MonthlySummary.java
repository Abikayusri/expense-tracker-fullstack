package com.abika.expense.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public record MonthlySummary(long totalIncome, long totalExpense, List<CategorySummary> perCategory) {

    public long balance() {
        return totalIncome - totalExpense;
    }

    public static MonthlySummary of(List<Category> categories, Map<Long, Long> totalsByCategoryId) {
        long income = 0;
        long expense = 0;
        List<CategorySummary> perCategory = new ArrayList<>();
        for (Category category : categories) {
            long spent = totalsByCategoryId.getOrDefault(category.id(), 0L);
            if (category.type() == CategoryType.INCOME) {
                income += spent;
            } else {
                expense += spent;
            }
            perCategory.add(new CategorySummary(category.id(), category.name(), category.type(), spent, category.monthlyBudget()));
        }
        return new MonthlySummary(income, expense, List.copyOf(perCategory));
    }
}
