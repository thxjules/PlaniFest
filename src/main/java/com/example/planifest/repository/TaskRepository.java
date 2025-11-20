package com.example.planifest.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.planifest.entity.Task;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    Optional<Task> findByName(String name);

    long countByStatus(String status);

    boolean existsByNameAndDateAndUser_IdAndEvent_Id(
            String name,
            LocalDateTime date,
            Long userId,
            Long eventId
    );

    boolean existsByNameAndDateBetweenAndUser_IdAndEvent_Id(
            String name,
            LocalDateTime start,
            LocalDateTime end,
            Long userId,
            Long eventId
    );

    Optional<Task> findTopByUser_IdAndEvent_IdOrderByDateDesc(Long userId, Long eventId);

    List<Task> findByEvent_IdOrderByDateAsc(Long eventId);

    @Query("""
        SELECT t FROM Task t
        WHERE (:nombre IS NULL OR LOWER(t.name) LIKE LOWER(CONCAT('%', :nombre, '%')))
        AND (:estado IS NULL OR t.status = :estado)
        AND (:usuarioId IS NULL OR t.user.id = :usuarioId)
        AND (:fecha IS NULL OR DATE(t.date) = :fecha)
        """)
    List<Task> filtrarTareas(
            String nombre,
            String estado,
            Long usuarioId,
            LocalDate fecha
    );

}
