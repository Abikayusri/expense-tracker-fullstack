package com.abika.expense.config;

import com.abika.expense.application.port.in.AuthUseCase;
import com.abika.expense.application.port.in.CategoryUseCase;
import com.abika.expense.application.port.in.TransactionUseCase;
import com.abika.expense.application.port.out.CategoryRepository;
import com.abika.expense.application.port.out.PasswordHasher;
import com.abika.expense.application.port.out.TokenIssuer;
import com.abika.expense.application.port.out.TransactionRepository;
import com.abika.expense.application.port.out.UserRepository;
import com.abika.expense.application.service.AuthService;
import com.abika.expense.application.service.CategoryService;
import com.abika.expense.application.service.TransactionService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

@Configuration
public class UseCaseConfig {

    @Bean
    Clock clock() {
        return Clock.systemDefaultZone();
    }

    @Bean
    AuthUseCase authUseCase(UserRepository users, PasswordHasher passwordHasher, TokenIssuer tokenIssuer, Clock clock) {
        return new AuthService(users, passwordHasher, tokenIssuer, clock);
    }

    // shortcut: budget alerts don't exist yet, so clearing them is a no-op; replaced by the real adapter in the budget-alert feature.
    @Bean
    CategoryUseCase categoryUseCase(CategoryRepository categories, TransactionRepository transactions) {
        return new CategoryService(categories, transactions, categoryId -> { });
    }

    // shortcut: no budget check yet, so saved expenses trigger nothing; replaced by BudgetCheckService in the budget-alert feature.
    @Bean
    TransactionUseCase transactionUseCase(TransactionRepository transactions, CategoryRepository categories) {
        return new TransactionService(transactions, categories, (saved, category) -> { });
    }
}
