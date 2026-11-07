package com.abika.expense.adapter.out.persistence;

import com.abika.expense.application.port.out.BudgetAlertRepository;
import com.abika.expense.domain.BudgetAlert;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class BudgetAlertPersistenceAdapter implements BudgetAlertRepository {

    private final NamedParameterJdbcTemplate jdbc;

    BudgetAlertPersistenceAdapter(NamedParameterJdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public boolean insertIfAbsent(BudgetAlert alert) {
        int inserted = jdbc.update("""
                        INSERT INTO budget_alerts (user_id, category_id, year_month)
                        VALUES (:userId, :categoryId, :yearMonth)
                        ON CONFLICT DO NOTHING""",
                Map.of("userId", alert.userId(), "categoryId", alert.categoryId(), "yearMonth", alert.month().toString()));
        return inserted == 1;
    }

    @Override
    public void deleteByCategory(Long categoryId) {
        jdbc.update("DELETE FROM budget_alerts WHERE category_id = :categoryId", Map.of("categoryId", categoryId));
    }
}
