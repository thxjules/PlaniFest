package com.example.planifest.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.planifest.entity.Event;
import com.example.planifest.enums.EventStatus;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    // Listar por nombre completo de evento
    List<Event> findByEventName(String eventName);

    // Listar por fecha exacta
    List<Event> findByDate(LocalDate date);

    // Listar por estado del evento
    List<Event> findByStatus(EventStatus status);

    // Buscar eventos posteriores a una fecha
    List<Event> findByDateAfter(LocalDate date);

}
