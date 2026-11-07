package com.abika.expense.domain;

public record CategorySummary(Long categoryId, String name, CategoryType type, long spent, Long budget) {

    public boolean overBudget() {
        return budget != null && spent > budget;
    }
}
