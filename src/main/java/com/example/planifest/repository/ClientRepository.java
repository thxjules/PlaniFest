package com.example.planifest.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.planifest.entity.Client;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {
 boolean existsByEmailIgnoreCase(String email);
}