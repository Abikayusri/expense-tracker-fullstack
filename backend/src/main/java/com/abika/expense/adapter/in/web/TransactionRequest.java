package com.abika.expense.adapter.in.web;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record TransactionRequest(
        @NotNull Long categoryId,
        @NotNull @Positive Long amount,
        @NotNull LocalDate date,
        @Size(max = 255) String note) {
}
