package com.example.planifest.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.planifest.entity.Event;
import com.example.planifest.repository.EventRepository;
import com.example.planifest.service.dao.Idao;

@Service
public class EventServiceImp implements Idao<Event, Long> {

    private final EventRepository eventRepository;

    public EventServiceImp(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    @Override
    public List<Event> getAll() {
        return eventRepository.findAll();
    }

    @Override
    public void create(Event event) {
        validateEvent(event);
        eventRepository.save(event);
    }

    @Override
    public void update(Event event) {
        if (event.getId() == null || !eventRepository.existsById(event.getId())) {
            throw new RuntimeException("No se puede actualizar el evento porque no existe.");
        }
        validateEvent(event);
        eventRepository.save(event);
    }

    @Override
    public void deleteById(Long id) {
        if (!eventRepository.existsById(id)) {
            throw new RuntimeException("No se puede eliminar el evento porque no existe.");
        }
        eventRepository.deleteById(id);
    }

    private void validateEvent(Event event) {
        if (isBlank(event.getEventName())) {
            throw new RuntimeException("El nombre del evento es obligatorio.");
        }
        if (isBlank(event.getDescription())) {
            throw new RuntimeException("La descripción del evento es obligatoria.");
        }
        if (event.getDate() == null) {
            throw new RuntimeException("La fecha del evento es obligatoria.");
        }
        if (event.getStartTime() == null || event.getEndTime() == null) {
            throw new RuntimeException("La hora de inicio y fin del evento es obligatoria.");
        }
        if (event.getEndTime().isBefore(event.getStartTime())) {
            throw new RuntimeException("La hora de fin no puede ser anterior a la hora de inicio.");
        }
        if (event.getGuestCount() == null || event.getGuestCount() <= 0) {
            throw new RuntimeException("El número de invitados debe ser mayor a cero.");
        }
        if (event.getLocation() == null) {
            throw new RuntimeException("La ubicación del evento es obligatoria.");
        }
        if (event.getStatus() == null) {
            throw new RuntimeException("El estado del evento es obligatorio.");
        }
        if (event.getClient() == null || event.getClient().getId() == null) {
            throw new RuntimeException("El evento debe tener un cliente válido asignado.");
        }
    }

    private boolean isBlank(String str) {
        return str == null || str.isBlank();
    }
}
