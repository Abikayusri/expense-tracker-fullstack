package com.abika.expense.adapter.out.persistence;

import com.abika.expense.application.port.out.UserRepository;
import com.abika.expense.domain.User;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class UserPersistenceAdapter implements UserRepository {

    private final UserJpaRepository jpa;

    UserPersistenceAdapter(UserJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public Optional<User> findByEmail(String normalizedEmail) {
        return jpa.findByEmail(normalizedEmail).map(UserPersistenceAdapter::toDomain);
    }

    @Override
    public Optional<User> findById(Long id) {
        return jpa.findById(id).map(UserPersistenceAdapter::toDomain);
    }

    @Override
    public boolean existsByEmail(String normalizedEmail) {
        return jpa.existsByEmail(normalizedEmail);
    }

    @Override
    public User save(User user) {
        UserEntity entity = new UserEntity(user.id(), user.email(), user.passwordHash(), user.name(), user.createdAt());
        return toDomain(jpa.save(entity));
    }

    private static User toDomain(UserEntity e) {
        return new User(e.getId(), e.getEmail(), e.getPasswordHash(), e.getName(), e.getCreatedAt());
    }
}
