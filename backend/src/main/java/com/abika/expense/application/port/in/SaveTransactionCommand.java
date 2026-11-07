package com.abika.expense.application.port.in;

import java.time.LocalDate;

public record SaveTransactionCommand(Long userId, Long categoryId, long amount, LocalDate date, String note) {
}
