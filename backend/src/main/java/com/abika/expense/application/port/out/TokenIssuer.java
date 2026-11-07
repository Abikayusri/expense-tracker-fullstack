package com.abika.expense.application.port.out;

public interface TokenIssuer {

    String issue(Long userId);
}
