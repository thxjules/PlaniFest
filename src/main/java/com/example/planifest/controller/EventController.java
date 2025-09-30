package com.example.planifest.controller;

import com.example.planifest.entity.Event;
import com.example.planifest.service.EmailServiceUser;
import com.example.planifest.service.EventServiceImp;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

        if (newEvent.getClient() != null && newEvent.getClient().getEmail() != null) {
            emailService.enviarCorreoACliente(
                    newEvent.getClient().getEmail(),
                    "Confirmación de evento: " + newEvent.getEventName(),
                    "Hola " + newEvent.getClient().getName() +
                            ", tu evento ha sido registrado exitosamente para el " + newEvent.getDate() + "."
            );
        } else {
            System.out.println("⚠️ No se envió correo porque el evento no tiene cliente con email.");
        }

        return ResponseEntity.status(201).body(newEvent);
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
