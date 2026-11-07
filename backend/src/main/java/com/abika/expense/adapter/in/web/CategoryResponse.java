package com.abika.expense.adapter.in.web;

import com.abika.expense.domain.Category;
import com.abika.expense.domain.CategoryType;

public record CategoryResponse(Long id, String name, CategoryType type, Long monthlyBudget) {

    static CategoryResponse from(Category category) {
        return new CategoryResponse(category.id(), category.name(), category.type(), category.monthlyBudget());
    }
}
