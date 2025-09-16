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
import com.example.planifest.service.EmailServiceUser;
import com.example.planifest.service.EventServiceImp;

@RestController
@RequestMapping("/events")
public class EventController {

    private final EmailServiceUser emailService;
    private final EventServiceImp eventService;

    public EventController(EventServiceImp eventService, EmailServiceUser emailService) {
        this.eventService = eventService;
        this.emailService = emailService;
    }

    @GetMapping
    public ResponseEntity<List<Event>> getAllEvents() {
        return ResponseEntity.ok(eventService.getAll());
    }

    @PostMapping
    public ResponseEntity<?> createEvent(@RequestBody Event event) {
     Event newEvent = eventService.createAndReturn(event);
     
    emailService.enviarCorreoACliente(
        newEvent.getClient().getEmail(),
        "Confirmación de evento: " + newEvent.getEventName(),
        "Hola " + newEvent.getClient().getName() +
        ", tu evento ha sido registrado exitosamente para el " + newEvent.getDate() + "."
    );

        return ResponseEntity.status(201).build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateEvent(@PathVariable Long id, @RequestBody Event event) {
        event.setId(id);
        eventService.update(event);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteEvent(@PathVariable Long id) {
        eventService.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
