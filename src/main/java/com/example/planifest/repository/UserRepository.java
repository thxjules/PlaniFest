package com.example.planifest.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.planifest.entity.User;
import com.example.planifest.enums.Role;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    User findByUsername(String username);

    Optional<User> findByEmail(String email);

    User findByRole(Role role);

    Long countByRole(Role role);
    boolean existsByUsername(String username);

}
