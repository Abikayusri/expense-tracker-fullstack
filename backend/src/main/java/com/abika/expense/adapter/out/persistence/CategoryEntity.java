package com.abika.expense.adapter.out.persistence;

import com.abika.expense.domain.CategoryType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "categories")
class CategoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CategoryType type;

    @Column(name = "monthly_budget")
    private Long monthlyBudget;

    protected CategoryEntity() {
    }

    CategoryEntity(Long id, Long userId, String name, CategoryType type, Long monthlyBudget) {
        this.id = id;
        this.userId = userId;
        this.name = name;
        this.type = type;
        this.monthlyBudget = monthlyBudget;
    }

    Long getId() {
        return id;
    }

    Long getUserId() {
        return userId;
    }

    String getName() {
        return name;
    }

    CategoryType getType() {
        return type;
    }

    Long getMonthlyBudget() {
        return monthlyBudget;
    }
}
