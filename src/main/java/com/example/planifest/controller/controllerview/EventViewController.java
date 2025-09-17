package com.example.planifest.controller.controllerview;

import com.example.planifest.entity.Event;
import com.example.planifest.entity.EventSupply;
import com.example.planifest.service.ClientServiceImp;
import com.example.planifest.service.EventServiceImp;
import com.example.planifest.service.SupplyServiceImp;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/events-view")
public class EventViewController {

    private final EventServiceImp eventService;
    private final ClientServiceImp clientService;
    private final SupplyServiceImp supplyService;

    public EventViewController(EventServiceImp eventService, ClientServiceImp clientService,
            SupplyServiceImp supplyService) {
        this.eventService = eventService;
        this.clientService = clientService;
        this.supplyService = supplyService;
    }

    @GetMapping
    public String mostrarEventos(
            @RequestParam(name = "id", required = false) Long id,
            @RequestParam(name = "nombre", required = false) String nombre,
            @RequestParam(name = "minGuests", required = false) Integer minGuests,
            @RequestParam(name = "clientId", required = false) Long clientId,
            Model model) {
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
        model.addAttribute("suministros", supplyService.getAll()); // Supply

        return "events";
    }

    @PostMapping("/save")
public String guardarEvento(@ModelAttribute Event event, Model model, RedirectAttributes redirectAttributes) {
    try {
        if (event.getId() == null) {
            eventService.create(event);
            redirectAttributes.addFlashAttribute("successMessage", "Evento creado exitosamente.");
        } else {
            eventService.update(event);
            redirectAttributes.addFlashAttribute("successMessage", "Evento actualizado correctamente.");
        }
        return "redirect:/events-view";
    } catch (RuntimeException ex) {
        model.addAttribute("event", event);
        model.addAttribute("eventos", eventService.getAll());
        model.addAttribute("clientes", clientService.getAll());
        model.addAttribute("suministros", supplyService.getAll());
        model.addAttribute("errorMessage", ex.getMessage()); // clave para mostrar la alerta
        return "events"; // asegúrate de que esta vista tenga el bloque para mostrar el mensaje
    }
}


    @GetMapping("/delete/{id}")
    public String eliminarEvento(@PathVariable Long id, RedirectAttributes redirectAttributes) {
    try {
        eventService.deleteById(id);
        redirectAttributes.addFlashAttribute("successMessage", "Evento eliminado correctamente.");
    } catch (RuntimeException e) {
        redirectAttributes.addFlashAttribute("errorMessage", "Error al eliminar el evento: " + e.getMessage());
    }
    return "redirect:/events-view";
}
    }


