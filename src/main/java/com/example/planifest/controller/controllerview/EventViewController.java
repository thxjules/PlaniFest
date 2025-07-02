package com.example.planifest.controller.controllerview;

import com.example.planifest.entity.Event;
import com.example.planifest.service.ClientServiceImp;
import com.example.planifest.service.EventServiceImp;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/events-view")
public class EventViewController {

    private final EventServiceImp eventService;
    private final ClientServiceImp clientService;

    public EventViewController(EventServiceImp eventService, ClientServiceImp clientService) {
        this.eventService = eventService;
        this.clientService = clientService;
    }

    @GetMapping
    public String mostrarEventos(
            @RequestParam(name = "id", required = false) Long id,
            @RequestParam(name = "nombre", required = false) String nombre,
            @RequestParam(name = "minGuests", required = false) Integer minGuests,
            @RequestParam(name = "clientId", required = false) Long clientId,
            Model model
    ) {
        Event event = (id != null) ? eventService.findById(id).orElse(new Event()) : new Event();
        List<Event> eventos = eventService.getAll();

        // Aplicar filtros
        if (nombre != null && !nombre.isBlank()) {
            eventos = eventos.stream()
                    .filter(e -> e.getEventName().toLowerCase().contains(nombre.toLowerCase()))
                    .collect(Collectors.toList());
        }

        if (minGuests != null) {
            eventos = eventos.stream()
                    .filter(e -> e.getGuestCount() >= minGuests)
                    .collect(Collectors.toList());
        }

        if (clientId != null) {
            eventos = eventos.stream()
                    .filter(e -> e.getClient() != null && e.getClient().getId().equals(clientId))
                    .collect(Collectors.toList());
        }

        model.addAttribute("event", event);
        model.addAttribute("eventos", eventos);
        model.addAttribute("clientes", clientService.getAll());

        return "events";
    }

    @PostMapping("/save")
    public String guardarEvento(@ModelAttribute Event event, Model model) {
        try {
            if (event.getId() == null) {
                eventService.create(event);
            } else {
                eventService.update(event);
            }
            return "redirect:/events-view";
        } catch (RuntimeException ex) {
            model.addAttribute("event", event);
            model.addAttribute("eventos", eventService.getAll());
            model.addAttribute("clientes", clientService.getAll());
            model.addAttribute("error", ex.getMessage());
            return "events";
        }
    }

    @GetMapping("/delete/{id}")
    public String eliminarEvento(@PathVariable Long id) {
        eventService.deleteById(id);
        return "redirect:/events-view";
    }
}
