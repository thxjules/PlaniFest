package com.example.planifest.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

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
@Transactional
public void update(Event event) {
    if (event.getId() == null || !eventRepository.existsById(event.getId())) {
        throw new RuntimeException("No se puede actualizar el evento porque no existe.");
    }

    validateEvent(event);

    // Obtengo los EventSupply actuales en BD (antes de la edición)
    List<EventSupply> existingList = eventSupplyRepository.findByEventId(event.getId());
    Map<Long, EventSupply> existingById = existingList.stream()
            .filter(es -> es.getId() != null)
            .collect(Collectors.toMap(EventSupply::getId, Function.identity()));

    List<EventSupply> submitted = event.getEventSupplies() != null ? event.getEventSupplies() : new ArrayList<>();

    for (EventSupply subEs : submitted) {
        if (subEs.getSupply() == null || subEs.getSupply().getId() == null) {
            throw new RuntimeException("Debe seleccionar un suministro válido.");
        }

        Supply newSupply = supplyRepository.findById(subEs.getSupply().getId())
                .orElseThrow(() -> new RuntimeException("El suministro no se ha encontrado."));

        if (subEs.getQuantitySupply() <= 0) {
            throw new RuntimeException("La cantidad del suministro debe ser mayor a 0.");
        }

        // Si tiene id, era un registro existente -> comprobar cambios
        if (subEs.getId() != null && existingById.containsKey(subEs.getId())) {
            EventSupply oldEs = existingById.remove(subEs.getId()); // lo saco del mapa (queda lo eliminado)

            Supply oldSupply = oldEs.getSupply();
            int oldQty = oldEs.getQuantitySupply();
            int newQty = subEs.getQuantitySupply();

            if (oldSupply.getId().equals(newSupply.getId())) {
                // mismo suministro: aplico delta
                int delta = newQty - oldQty;
                if (delta > 0) {
                    if (newSupply.getCurrentStock() < delta) {
                        throw new RuntimeException("Stock insuficiente para el suministro: " + newSupply.getName());
                    }
                    newSupply.setCurrentStock(newSupply.getCurrentStock() - delta);
                } else if (delta < 0) {
                    // delta negativo: devolvemos (-delta) al stock
                    newSupply.setCurrentStock(newSupply.getCurrentStock() - delta); // restando un negativo = sumando
                }
                supplyRepository.save(newSupply);
            } else {
                // el usuario cambió el suministro: devolver qty al oldSupply y descontar en newSupply
                oldSupply.setCurrentStock(oldSupply.getCurrentStock() + oldQty);
                supplyRepository.save(oldSupply);

                if (newSupply.getCurrentStock() < newQty) {
                    throw new RuntimeException("Stock insuficiente para el suministro: " + newSupply.getName());
                }
                newSupply.setCurrentStock(newSupply.getCurrentStock() - newQty);
                supplyRepository.save(newSupply);
            }

            // Asignaciones necesarias para persistir correctamente
            subEs.setSupply(newSupply);
            subEs.setEvent(event);
        } else {
            // nuevo EventSupply (no existía antes)
            int qty = subEs.getQuantitySupply();
            if (newSupply.getCurrentStock() < qty) {
                throw new RuntimeException("Stock insuficiente para el suministro: " + newSupply.getName());
            }
            newSupply.setCurrentStock(newSupply.getCurrentStock() - qty);
            supplyRepository.save(newSupply);

            subEs.setSupply(newSupply);
            subEs.setEvent(event);
        }
    }

    for (EventSupply removed : existingById.values()) {
        Supply s = supplyRepository.findById(removed.getSupply().getId()).orElse(null);
        if (s != null) {
            s.setCurrentStock(s.getCurrentStock() + removed.getQuantitySupply());
            supplyRepository.save(s);
        }
        eventSupplyRepository.delete(removed);
    }

    // Finalmente guardo el evento (CascadeType.ALL guarda/actualiza EventSupply)
    eventRepository.save(event);
}


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