package com.abika.expense.adapter.in.web;

import com.abika.expense.domain.Transaction;

import java.time.LocalDate;

public record TransactionResponse(Long id, Long categoryId, long amount, LocalDate date, String note) {

    static TransactionResponse from(Transaction t) {
        return new TransactionResponse(t.id(), t.categoryId(), t.amount(), t.date(), t.note());
    }
}
