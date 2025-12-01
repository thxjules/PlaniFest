package com.example.planifest.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.planifest.entity.EventSupply;

@Repository
public interface EventSupplyRepository extends JpaRepository<EventSupply, Long>{

    List<EventSupply> findByEventId(Long eventId);

    List<EventSupply> findByDeletedFalse();

    @Query("SELECT es FROM EventSupply es JOIN FETCH es.supply WHERE es.event.id = :eventId")
    List<EventSupply> findByEventIdWithSupply(@Param("eventId") Long eventId);
}