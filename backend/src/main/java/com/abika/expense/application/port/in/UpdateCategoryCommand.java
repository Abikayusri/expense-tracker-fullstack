package com.abika.expense.application.port.in;

public record UpdateCategoryCommand(Long userId, Long id, String name, Long monthlyBudget) {
}
