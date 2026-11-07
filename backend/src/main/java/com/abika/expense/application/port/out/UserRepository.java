package com.abika.expense.application.port.out;

import com.abika.expense.domain.User;

import java.util.Optional;

public interface UserRepository {

    Optional<User> findByEmail(String normalizedEmail);

    Optional<User> findById(Long id);

    boolean existsByEmail(String normalizedEmail);

    User save(User user);
}
