package com.example.planifest.controller.controllerview;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute; 
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.planifest.entity.Event;
import com.example.planifest.entity.PlaniService;
import com.example.planifest.service.ClientServiceImp;
import com.example.planifest.service.EventServiceImp;
import com.example.planifest.service.MapService;
import com.example.planifest.service.PlaniServiceImp;
import com.example.planifest.service.SupplyServiceImp;

@Controller
@RequestMapping("/events-view")
public class EventViewController {

    private final EventServiceImp eventService;
    private final ClientServiceImp clientService;
    private final SupplyServiceImp supplyService;
    private final MapService mapService;
    private final PlaniServiceImp planiService;

    // 🔧 Inyecta la API Key de Google Maps desde application.properties
    @Value("${google.maps.api.key}")
    private String googleMapsApiKey;

    public EventViewController(EventServiceImp eventService,
                               ClientServiceImp clientService,
                               SupplyServiceImp supplyService,
                               MapService mapService,
                               PlaniServiceImp planiService) {
        this.eventService = eventService;
        this.clientService = clientService;
        this.supplyService = supplyService;
        this.mapService = mapService;
        this.planiService = planiService;
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
        model.addAttribute("suministros", supplyService.getAll());
        model.addAttribute("allservicios", planiService.getAll());
        model.addAttribute("googleMapsApiKey", googleMapsApiKey);

        // Genera la URL segura del mapa embebido
        String mapUrl = (event.getLocation() != null && !event.getLocation().isBlank()) 
                        ? "https://www.google.com/maps/embed/v1/place?key=" + googleMapsApiKey +
                          "&q=" + event.getLocation().replace(" ", "+") 
                        : null;
        model.addAttribute("mapUrl", mapUrl);

        return "events";
    }

    @PostMapping("/save")
    public String guardarEvento(@ModelAttribute Event event, Model model, RedirectAttributes redirectAttributes) {
        try {
            if (event.getPlaniServices() != null) {
                List<PlaniService> serviciosPersistidos = event.getPlaniServices().stream()
                    .map(s -> planiService.getAll().stream()
                        .filter(p -> p.getServiceId().equals(s.getServiceId()))
                        .findFirst()
                        .orElseThrow(() -> new RuntimeException("Servicio no encontrado: " + s.getName()))
                    )
                    .collect(Collectors.toList());
                event.setPlaniServices(serviciosPersistidos);
            }

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
            model.addAttribute("allservicios", planiService.getAll());
            model.addAttribute("errorMessage", ex.getMessage());
            model.addAttribute("googleMapsApiKey", googleMapsApiKey);
            return "events";
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
