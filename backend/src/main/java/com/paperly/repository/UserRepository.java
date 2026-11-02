package com.paperly.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.paperly.entity.User;
import com.paperly.entity.UserRole;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    Optional<User> findByUsernameIgnoreCase(String username);

    long countByRole(UserRole role);
}
