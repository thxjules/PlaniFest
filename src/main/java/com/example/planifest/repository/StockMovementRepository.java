package com.example.planifest.repository;

import java.time.LocalDate;
import java.util.List;

import org.hibernate.Remove;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.planifest.entity.StockMovement;
import com.example.planifest.entity.Supply;

@Remove
public interface StockMovementRepository extends JpaRepository<StockMovement, Long> {

    List<StockMovement> findByDeletedFalse();
    List<StockMovement> findByDate(LocalDate date);
        List<StockMovement> findBySupplyAndDeletedFalse(Supply supply);
}
