package com.example.planifest.repository;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.planifest.entity.Task;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    Optional<Task> findByName(String name);
    
    boolean existsByNameAndDateAndUser_IdAndEvent_Id(
            String name,
            LocalDateTime date,
            Long userId,
            Long eventId
    );
}

