package com.abika.expense.application.fake;

import com.abika.expense.application.port.out.UserRepository;
import com.abika.expense.domain.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class InMemoryUserRepository implements UserRepository {

    private final List<User> users = new ArrayList<>();
    private long nextId = 1;

    @Override
    public Optional<User> findByEmail(String normalizedEmail) {
        return users.stream().filter(u -> u.email().equals(normalizedEmail)).findFirst();
    }

    @Override
    public Optional<User> findById(Long id) {
        return users.stream().filter(u -> u.id().equals(id)).findFirst();
    }

    @Override
    public boolean existsByEmail(String normalizedEmail) {
        return findByEmail(normalizedEmail).isPresent();
    }

    @Override
    public User save(User user) {
        User saved = user.id() == null
                ? new User(nextId++, user.email(), user.passwordHash(), user.name(), user.createdAt())
                : user;
        users.removeIf(u -> u.id().equals(saved.id()));
        users.add(saved);
        return saved;
    }

    public List<User> all() {
        return List.copyOf(users);
    }
}
