package com.abika.expense.adapter.in.web;

import com.abika.expense.domain.CategoryType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record CategoryRequest(
        @NotBlank @Size(max = 50) String name,
        @NotNull CategoryType type,
        @Positive Long monthlyBudget) {
}
