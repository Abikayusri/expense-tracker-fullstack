package com.abika.expense.adapter.in.web;

import com.abika.expense.domain.CategorySummary;
import com.abika.expense.domain.CategoryType;

public record CategorySummaryResponse(Long categoryId, String name, CategoryType type, long spent, Long budget,
                                      boolean overBudget) {

    static CategorySummaryResponse from(CategorySummary s) {
        return new CategorySummaryResponse(s.categoryId(), s.name(), s.type(), s.spent(), s.budget(), s.overBudget());
    }
}
