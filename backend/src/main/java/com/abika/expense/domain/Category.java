package com.abika.expense.domain;

import com.abika.expense.domain.exception.DomainValidationException;

public record Category(Long id, Long userId, String name, CategoryType type, Long monthlyBudget) {

    private static final int MAX_NAME_LENGTH = 50;

    public Category {
        if (name == null || name.isBlank()) {
            throw new DomainValidationException("Nama kategori wajib diisi");
        }
        name = name.trim();
        if (name.length() > MAX_NAME_LENGTH) {
            throw new DomainValidationException("Nama kategori maksimal " + MAX_NAME_LENGTH + " karakter");
        }
        if (type == null) {
            throw new DomainValidationException("Tipe kategori wajib diisi");
        }
        if (monthlyBudget != null) {
            if (type == CategoryType.INCOME) {
                throw new DomainValidationException("Budget hanya untuk kategori pengeluaran");
            }
            if (monthlyBudget <= 0) {
                throw new DomainValidationException("Budget harus lebih dari 0");
            }
        }
    }

    public Category withChanges(String name, Long monthlyBudget) {
        return new Category(id, userId, name, type, monthlyBudget);
    }
}
