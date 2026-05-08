package com.library.repository;

import com.library.model.Role;
import com.library.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(String email);
    Optional<User> findByEmail(String email);
    Page<User> findByRoleNot(Role role, Pageable pageable);
    long countByRole(Role role);
}