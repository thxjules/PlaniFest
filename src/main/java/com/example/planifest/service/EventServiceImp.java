package com.example.planifest.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.planifest.entity.Event;
import com.example.planifest.entity.EventSupply;
import com.example.planifest.entity.Supply;
import com.example.planifest.repository.EventRepository;
import com.example.planifest.repository.EventSupplyRepository;
import com.example.planifest.repository.SupplyRepository;
import com.example.planifest.service.dao.Idao;

@Service
public class EventServiceImp implements Idao<Event, Long> {

    private final EventRepository eventRepository;
    private final SupplyRepository supplyRepository;
    private final EventSupplyRepository eventSupplyRepository;

    public EventServiceImp(EventRepository eventRepository, SupplyRepository supplyRepository,
            EventSupplyRepository eventSupplyRepository) {
        this.eventRepository = eventRepository;
        this.supplyRepository = supplyRepository;
        this.eventSupplyRepository = eventSupplyRepository;
    }

    @Override
    public List<Event> getAll() {
        return eventRepository.findAll();
    }

    public List<Event> findByClientId(Long clientId) {
        return eventRepository.findByClientId(clientId);
    }
  

    @Override
@Transactional
public void create(Event event) {
    validateEvent(event);

    // Asignar el evento a cada EventSupply ANTES de guardar el evento
    for (EventSupply es : event.getEventSupplies()) {
        if (es.getSupply() == null || es.getSupply().getId() == null) {
            throw new RuntimeException("Debe seleccionar un suministro válido.");
        }

        Supply supply = supplyRepository.findById(es.getSupply().getId())
            .orElseThrow(() -> new RuntimeException("El suministro no se ha encontrado."));

        if (es.getQuantitySupply() <= 0) {
            throw new RuntimeException("La cantidad del suministro debe ser mayor a 0.");
        }

        if (es.getQuantitySupply() > supply.getCurrentStock()) {
            throw new RuntimeException("La cantidad solicitada excede el stock disponible.");
        }

        // Descontar stock
        supply.setCurrentStock(supply.getCurrentStock() - es.getQuantitySupply());
        supplyRepository.save(supply);

        // Asignar relaciones
        es.setEvent(event); // ← Aquí usamos el objeto original, no el savedEvent
        es.setSupply(supply);
    }

    // Ahora sí, guardar el evento con sus suministros
    eventRepository.save(event); // gracias a CascadeType.ALL, se guardan los EventSupply
}


    
    public Event createAndReturn(Event event) {
    validateEvent(event);
    return eventRepository.save(event);
}


    @Override
    public void update(Event event) {
        if (event.getId() == null || !eventRepository.existsById(event.getId())) {
            throw new RuntimeException("No se puede actualizar el evento porque no existe.");
        }
        validateEvent(event);
        eventRepository.save(event);
    }
/*  
    @Override
    public void deleteById(Long id) {
        if (!eventRepository.existsById(id)) {
            throw new RuntimeException("No se puede eliminar el evento porque no existe.");
        }
        eventRepository.deleteById(id);
    }
 */

@Override
@Transactional
public void deleteById(Long id) {
    Event event = eventRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("No se puede eliminar el evento porque no existe."));

    List<EventSupply> supplies = eventSupplyRepository.findByEventId(id);

    for (EventSupply es : supplies) {
        Supply supply = es.getSupply();
        supply.setCurrentStock(supply.getCurrentStock() + es.getQuantitySupply());
        supplyRepository.save(supply);
    }

    eventSupplyRepository.deleteAll(supplies);
    eventRepository.delete(event);
}

    public long count() {
        return eventRepository.count();

    }

    public Optional<Event> findById(Long id) {
        return eventRepository.findById(id);
    }

    /* Validaciones de los atributos del Evento */

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