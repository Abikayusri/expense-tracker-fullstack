package com.abika.expense.config;

import com.abika.expense.application.port.in.AuthUseCase;
import com.abika.expense.application.port.in.CategoryUseCase;
import com.abika.expense.application.port.in.DeviceUseCase;
import com.abika.expense.application.port.in.ExpenseSavedListener;
import com.abika.expense.application.port.in.SummaryUseCase;
import com.abika.expense.application.port.in.TransactionUseCase;
import com.abika.expense.application.port.out.BudgetAlertRepository;
import com.abika.expense.application.port.out.CategoryRepository;
import com.abika.expense.application.port.out.DeviceTokenRepository;
import com.abika.expense.application.port.out.PasswordHasher;
import com.abika.expense.application.port.out.PushSender;
import com.abika.expense.application.port.out.TokenIssuer;
import com.abika.expense.application.port.out.TransactionRepository;
import com.abika.expense.application.port.out.UserRepository;
import com.abika.expense.application.service.AuthService;
import com.abika.expense.application.service.BudgetCheckService;
import com.abika.expense.application.service.CategoryService;
import com.abika.expense.application.service.DeviceService;
import com.abika.expense.application.service.SummaryService;
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

    @Bean
    CategoryUseCase categoryUseCase(CategoryRepository categories, TransactionRepository transactions,
                                    BudgetAlertRepository budgetAlerts) {
        return new CategoryService(categories, transactions, budgetAlerts);
    }

    @Bean
    ExpenseSavedListener budgetCheck(TransactionRepository transactions, BudgetAlertRepository budgetAlerts,
                                     DeviceTokenRepository devices, PushSender pushSender) {
        return new BudgetCheckService(transactions, budgetAlerts, devices, pushSender);
    }

    @Bean
    TransactionUseCase transactionUseCase(TransactionRepository transactions, CategoryRepository categories,
                                          ExpenseSavedListener budgetCheck) {
        return new TransactionService(transactions, categories, budgetCheck);
    }

    @Bean
    DeviceUseCase deviceUseCase(DeviceTokenRepository devices) {
        return new DeviceService(devices);
    }

    @Bean
    SummaryUseCase summaryUseCase(CategoryRepository categories, TransactionRepository transactions) {
        return new SummaryService(categories, transactions);
    }
}
