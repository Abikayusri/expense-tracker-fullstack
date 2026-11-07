package com.abika.expense.application.port.in;

public record RegisterCommand(String email, String password, String name) {
}
