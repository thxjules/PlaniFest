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
        eventRepository.save(event);
    }

    @Override
    public void update(Event event) {
        if (event.getId() != null && eventRepository.existsById(event.getId())) {
            eventRepository.save(event);
        } else {
            throw new RuntimeException("No se puede actualizar el evento porque no existe.");
        }
    }

    @Override
    public void deleteById(Long id) {
        if (eventRepository.existsById(id)) {
            eventRepository.deleteById(id);
        } else {
            throw new RuntimeException("No se puede eliminar el evento porque no existe.");
        }
    }
}
