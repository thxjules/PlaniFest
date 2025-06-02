package com.example.planifest.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.planifest.entity.Supply;

@Repository
public interface SupplyRepository extends JpaRepository<Supply, Long> {

    // Consulta por nombre
    List<Supply> findByName(String name);

    //Consulta por el tipo de resursos (Moviliario)
    List<Supply> findBySupplyType(String supplyType);

    //Consulta por el stock actual
    List<Supply> findByCurrentStock(int currentStock);
}
