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
        if (eventRepository.existsById(id)) {
            eventRepository.deleteById(id);
        } else {
            throw new RuntimeException("No se puede eliminar el evento porque no existe.");
        }
    }

    private void validateEvent(Event event) {
        if (event.getEventName() == null || event.getEventName().isBlank()) {
            throw new RuntimeException("No se puede crear un evento con un nombre vacío");
        }

        if (event.getDescription() == null || event.getDescription().isBlank()) {
            throw new RuntimeException("No se puede crear un evento con una descripción vacía");
        }

        if (event.getDate() == null) {
            throw new RuntimeException("La fecha para crear un evento es obligatoria");
        }

        if (event.getStartTime() == null || event.getEndTime() == null) {
            throw new RuntimeException("La hora de inicio y fin de el evento es obligatoria");
        }

        if (event.getEndTime().isBefore(event.getStartTime())) {
            throw new RuntimeException("La hora de finalización no debe ser antes de la de inicio");
        }

        if (event.getGuestCount() == null || event.getGuestCount() <= 0) {
            throw new RuntimeException("El numero de invitados debe ser superior a cero");
        }

        if (event.getLocation() == null) {
            throw new RuntimeException("La ubicación del evento no puede ser nula");
        }

        if (event.getStatus() == null) {
            throw new RuntimeException("Se requiere añadirle un estado a el evento (Activo u Inactivo)");

        }

        if (event.getClient() == null || event.getClient().getId() == null) {
            throw new RuntimeException("Es necesario asignar un cliente valido a el evento");
        } else
            eventRepository.save(event);
    }
}
