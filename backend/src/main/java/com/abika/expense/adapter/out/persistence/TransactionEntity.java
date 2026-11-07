package com.abika.expense.adapter.out.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDate;

@Entity
@Table(name = "transactions")
class TransactionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "category_id", nullable = false)
    private Long categoryId;

    @Column(nullable = false)
    private long amount;

    @Column(nullable = false)
    private LocalDate date;

    private String note;

    protected TransactionEntity() {
    }

    TransactionEntity(Long id, Long userId, Long categoryId, long amount, LocalDate date, String note) {
        this.id = id;
        this.userId = userId;
        this.categoryId = categoryId;
        this.amount = amount;
        this.date = date;
        this.note = note;
    }

    Long getId() {
        return id;
    }

    Long getUserId() {
        return userId;
    }

    Long getCategoryId() {
        return categoryId;
    }

    long getAmount() {
        return amount;
    }

    LocalDate getDate() {
        return date;
    }

    String getNote() {
        return note;
    }
}
