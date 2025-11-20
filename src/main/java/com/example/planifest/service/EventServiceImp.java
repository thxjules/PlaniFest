package com.example.planifest.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.planifest.entity.Client;
import com.example.planifest.entity.Event;
import com.example.planifest.entity.EventSupply;
import com.example.planifest.entity.PlaniService;
import com.example.planifest.entity.Supply;
import com.example.planifest.repository.ClientRepository;
import com.example.planifest.repository.EventRepository;
import com.example.planifest.repository.EventSupplyRepository;
import com.example.planifest.repository.PlaniServiceRepository;
import com.example.planifest.repository.SupplyRepository;
import com.example.planifest.service.dao.Idao;

@Service
public class EventServiceImp implements Idao<Event, Long> {

    private final EventRepository eventRepository;
    private final SupplyRepository supplyRepository;
    private final EventSupplyRepository eventSupplyRepository;
    private final ClientRepository clientRepository;
    private final PlaniServiceRepository planiServiceRepository; 

    public EventServiceImp(EventRepository eventRepository,
            SupplyRepository supplyRepository,
            EventSupplyRepository eventSupplyRepository,
            ClientRepository clientRepository,
            PlaniServiceRepository planiServiceRepository) { 
        this.eventRepository = eventRepository;
        this.supplyRepository = supplyRepository;
        this.eventSupplyRepository = eventSupplyRepository;
        this.clientRepository = clientRepository;
        this.planiServiceRepository = planiServiceRepository;
    }

    /* ================== CREAR EVENTO ================== */

    @Override
    @Transactional
    public void create(Event event) {
        saveEventWithSuppliesAndServices(event, null);
    }

    @Transactional
    public Event createAndReturn(Event event) {
        return saveEventWithSuppliesAndServices(event, null);
    }

    // 👇 Método central que maneja suministros y servicios
    private Event saveEventWithSuppliesAndServices(Event event, List<Long> serviceIds) {
        validateEvent(event);

        // Cliente
        if (event.getClient() != null && event.getClient().getId() != null) {
            Client client = clientRepository.findById(event.getClient().getId())
                    .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));
            event.setClient(client);
        }

        // Manejo de suministros (igual que antes)
        if (event.getEventSupplies() != null) {
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

                supply.setCurrentStock(supply.getCurrentStock() - es.getQuantitySupply());
                supplyRepository.save(supply);

                es.setEvent(event);
                es.setSupply(supply);
            }
        }

        // 👇 Manejo de servicios asociados
        if (serviceIds != null && !serviceIds.isEmpty()) {
            List<PlaniService> servicios = planiServiceRepository.findAllById(serviceIds);
            event.setPlaniServices(servicios);
        }

        return eventRepository.save(event);
    }

    /* ================== GUARDAR EVENTO CON SERVICIOS ================== */

    @Transactional
    public Event saveWithServices(Event event, List<Long> serviceIds) {
        return saveEventWithSuppliesAndServices(event, serviceIds);
    }

    /* ================== OBTENER EVENTOS ================== */

    @Override
    public List<Event> getAll() {
        return eventRepository.findAll()
        .stream()
        .filter(e -> !e.isDeleted())
        .collect(Collectors.toList());
    }

    public List<Event> findByClientId(Long clientId) {
        return eventRepository.findByClientId(clientId);
    }

    /* ================== ACTUALIZAR EVENTO ================== */

    @Override
    @Transactional
    public void update(Event event) {
        if (event.getId() == null || !eventRepository.existsById(event.getId())) {
            throw new RuntimeException("No se puede actualizar el evento porque no existe.");
        }

        validateEvent(event);

        // 🔸 Actualizar servicios (sin tocar suministros aún)
        if (event.getPlaniServices() != null) {
            List<Long> ids = event.getPlaniServices().stream()
                    .map(PlaniService::getServiceId)
                    .toList();
            List<PlaniService> serviciosActualizados = planiServiceRepository.findAllById(ids);
            event.setPlaniServices(serviciosActualizados);
        }

        // 🔸 Luego manejar suministros igual que antes
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

            if (subEs.getId() != null && existingById.containsKey(subEs.getId())) {
                EventSupply oldEs = existingById.remove(subEs.getId());
                Supply oldSupply = oldEs.getSupply();
                int oldQty = oldEs.getQuantitySupply();
                int newQty = subEs.getQuantitySupply();

                if (oldSupply.getId().equals(newSupply.getId())) {
                    int delta = newQty - oldQty;
                    if (delta > 0) {
                        if (newSupply.getCurrentStock() < delta) {
                            throw new RuntimeException("Stock insuficiente para el suministro: " + newSupply.getName());
                        }
                        newSupply.setCurrentStock(newSupply.getCurrentStock() - delta);
                    } else if (delta < 0) {
                        newSupply.setCurrentStock(newSupply.getCurrentStock() - delta);
                    }
                    supplyRepository.save(newSupply);
                } else {
                    oldSupply.setCurrentStock(oldSupply.getCurrentStock() + oldQty);
                    supplyRepository.save(oldSupply);

                    if (newSupply.getCurrentStock() < newQty) {
                        throw new RuntimeException("Stock insuficiente para el suministro: " + newSupply.getName());
                    }
                    newSupply.setCurrentStock(newSupply.getCurrentStock() - newQty);
                    supplyRepository.save(newSupply);
                }

                subEs.setSupply(newSupply);
                subEs.setEvent(event);
            } else {
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

        eventRepository.save(event);
    }

    /* ================== ELIMINAR EVENTO ================== */

    @Override
    @Transactional
    public void deleteById(Long id) {

        //Validaciones Para encontrar el ID
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("No se puede eliminar el evento porque no existe."));

                //En caso de que el evento fue eliminado
        if(event.isDeleted()){
            throw new RuntimeException("El evento fue eliminado Anteriormente");
        }

        event.setDeleted(true);

        List<EventSupply> supplies = eventSupplyRepository.findByEventId(id);

        for (EventSupply es : supplies) {
            Supply supply = es.getSupply();

            //Devolucion al Stock actual
            supply.setCurrentStock(supply.getCurrentStock() + es.getQuantitySupply());
            supplyRepository.save(supply);
        

        //Valida la eliminacion de la relacion
        es.setDeleted(true);
        eventSupplyRepository.save(es);
        }

        event.getPlaniServices().clear(); 

        eventRepository.save(event);
    }

    /* ================== UTILIDADES ================== */

    public long count() {
        return eventRepository.count();
    }

    public Optional<Event> findById(Long id) {
        return eventRepository.findById(id);
    }

    private void validateEvent(Event event) {
        if (isBlank(event.getEventName()))
            throw new RuntimeException("El nombre del evento es obligatorio.");
        if (isBlank(event.getDescription()))
            throw new RuntimeException("La descripción del evento es obligatoria.");
        if (event.getDate() == null)
            throw new RuntimeException("La fecha del evento es obligatoria.");
        if (event.getStartTime() == null || event.getEndTime() == null)
            throw new RuntimeException("La hora de inicio y fin del evento es obligatoria.");
        if (event.getEndTime().isBefore(event.getStartTime()))
            throw new RuntimeException("La hora de fin no puede ser anterior a la de inicio.");
        if (event.getGuestCount() == null || event.getGuestCount() <= 0)
            throw new RuntimeException("El número de invitados debe ser mayor a cero.");
        if (event.getLocation() == null)
            throw new RuntimeException("La ubicación del evento es obligatoria.");
        if (event.getStatus() == null)
            throw new RuntimeException("El estado del evento es obligatorio.");
        if (event.getClient() == null || event.getClient().getId() == null)
            throw new RuntimeException("Debe tener un cliente válido asignado.");
        if (event.getDate().isBefore(LocalDate.now())) {
    throw new IllegalArgumentException("No se permiten fechas pasadas.");
}
}
    

    private boolean isBlank(String str) {
        return str == null || str.isBlank();
    }
}
