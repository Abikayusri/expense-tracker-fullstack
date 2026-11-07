package com.abika.expense.application.port.in;

import java.time.YearMonth;

public record TransactionQuery(Long userId, YearMonth month, Long categoryIdOrNull, int page, int size) {
}
