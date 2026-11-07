package com.abika.expense.application.fake;

import com.abika.expense.application.port.out.BudgetAlertRepository;
import com.abika.expense.domain.BudgetAlert;

import java.util.HashSet;
import java.util.Set;

public class InMemoryBudgetAlertRepository implements BudgetAlertRepository {

    private final Set<BudgetAlert> alerts = new HashSet<>();

    @Override
    public boolean insertIfAbsent(BudgetAlert alert) {
        return alerts.add(alert);
    }

    @Override
    public void deleteByCategory(Long categoryId) {
        alerts.removeIf(a -> a.categoryId().equals(categoryId));
    }

    public Set<BudgetAlert> all() {
        return Set.copyOf(alerts);
    }
}
