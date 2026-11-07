package com.abika.expense.adapter.in.web;

import com.abika.expense.domain.MonthlySummary;

import java.util.List;

public record SummaryResponse(long totalIncome, long totalExpense, long balance,
                              List<CategorySummaryResponse> perCategory) {

    static SummaryResponse from(MonthlySummary s) {
        return new SummaryResponse(s.totalIncome(), s.totalExpense(), s.balance(),
                s.perCategory().stream().map(CategorySummaryResponse::from).toList());
    }
}
