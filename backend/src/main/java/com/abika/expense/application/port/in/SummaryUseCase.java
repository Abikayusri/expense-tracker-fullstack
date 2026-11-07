package com.abika.expense.application.port.in;

import com.abika.expense.domain.MonthlySummary;

import java.time.YearMonth;

public interface SummaryUseCase {

    MonthlySummary get(Long userId, YearMonth month);
}
