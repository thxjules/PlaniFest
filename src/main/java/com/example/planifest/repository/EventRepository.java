package com.example.planifest.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.planifest.entity.Event;

@Repository
public interface EventRepository extends JpaRepository<Event, Long>{

}
