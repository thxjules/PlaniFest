package com.example.planifest.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.planifest.entity.Position;

@Repository
public interface PositionRepository extends JpaRepository<Position, Long> {

    //Verificar si ya existe un cargo con ese nombre
    boolean existsByNameIgnoreCase(String name);
}
