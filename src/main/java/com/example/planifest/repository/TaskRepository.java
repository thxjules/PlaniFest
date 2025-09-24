package com.example.planifest.repository;

import java.time.LocalDateTime;
import java.util.List;
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

    long countByStatus(String status);

    // ✅ Para obtener la última tarea de un usuario en un evento
    Optional<Task> findTopByUser_IdAndEvent_IdOrderByDateDesc(Long userId, Long eventId);

    // ✅ Para obtener todas las tareas de un evento ordenadas por fecha ascendente
    List<Task> findByEvent_IdOrderByDateAsc(Long eventId);
    boolean existsByNameAndDateBetweenAndUser_IdAndEvent_Id(
            String name, LocalDateTime start, LocalDateTime end,
            Long userId, Long eventId);
}
