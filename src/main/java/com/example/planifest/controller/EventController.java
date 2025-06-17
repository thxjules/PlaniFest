package com.example.planifest.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.planifest.entity.Event;
import com.example.planifest.service.EventServiceImp;

@RestController
@RequestMapping("/events")
public class EventController {

    private final EventServiceImp eventService;

    public EventController(EventServiceImp eventService) {
        this.eventService = eventService;
    }

    @GetMapping
    public ResponseEntity<List<Event>> getAllEvents() {
        return ResponseEntity.ok(eventService.getAll()); // status 200 + lista de eventos
    }

    @PostMapping
    public ResponseEntity<?> createEvent(@RequestBody Event event) {
        eventService.create(event);
        return ResponseEntity.status(201).build(); // status 201 creado
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateEvent(@PathVariable Long id, @RequestBody Event event) {
        event.setId(id);
        eventService.update(event);
        return ResponseEntity.noContent().build(); // status 204 (sin contenido)
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteEvent(@PathVariable Long id) {
        eventService.deleteById(id);
        return ResponseEntity.ok().build(); // status 200 OK
    }
}
