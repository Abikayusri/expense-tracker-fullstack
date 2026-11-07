package com.abika.expense.application.port.in;

import com.abika.expense.domain.CategoryType;

public record CreateCategoryCommand(Long userId, String name, CategoryType type, Long monthlyBudget) {
}
