package com.abika.expense.application.port.in;

import com.abika.expense.domain.User;

public interface AuthUseCase {

    String register(RegisterCommand command);

    String login(String email, String password);

    User me(Long userId);
}
