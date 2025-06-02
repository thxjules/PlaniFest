package com.example.planifest.repository;

import java.time.LocalDate;
import java.util.List;

import org.hibernate.Remove;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.planifest.entity.StockMovement;

@Remove
public interface StockMovementRepository extends JpaRepository<StockMovement, Long> {

    
    List<StockMovement> findByDate(LocalDate date);
}
