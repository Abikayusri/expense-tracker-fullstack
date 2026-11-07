package com.abika.expense.domain;

import java.time.YearMonth;

public record BudgetAlert(Long userId, Long categoryId, YearMonth month) {
}
