package com.example.planifest.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.planifest.entity.Event;
import com.example.planifest.enums.EventStatus;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    Optional<Event> findByEventName(String eventName); 

    List<Event> findByDate(LocalDate date);

    List<Event> findByStatus(EventStatus status);

    List<Event> findByDateAfter(LocalDate date);
}



