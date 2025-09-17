package com.example.planifest.repository;

import com.example.planifest.entity.PlaniService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlaniServiceRepository extends JpaRepository<PlaniService, Long> {
    
}
