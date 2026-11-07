package com.abika.expense.domain;

import com.abika.expense.domain.exception.DomainValidationException;

import java.time.LocalDate;
import java.time.YearMonth;

public record Transaction(Long id, Long userId, Long categoryId, long amount, LocalDate date, String note) {

    private static final int MAX_NOTE_LENGTH = 255;

    public Transaction {
        if (amount <= 0) {
            throw new DomainValidationException("Jumlah harus lebih dari 0");
        }
        if (date == null) {
            throw new DomainValidationException("Tanggal wajib diisi");
        }
        if (note != null && note.length() > MAX_NOTE_LENGTH) {
            throw new DomainValidationException("Catatan maksimal " + MAX_NOTE_LENGTH + " karakter");
        }
    }

    public YearMonth month() {
        return YearMonth.from(date);
    }
}
