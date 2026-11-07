package com.abika.expense;

import org.springframework.boot.SpringApplication;

public class TestExpenseApplication {

	public static void main(String[] args) {
		SpringApplication.from(ExpenseApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
