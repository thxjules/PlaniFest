package com.example.planifest.controller.controllerview;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.example.planifest.entity.Event;
import com.example.planifest.service.ClientServiceImp;
import com.example.planifest.service.EventServiceImp;

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
    public String mostrarEventos(@RequestParam(name = "id", required = false) Long id, Model model) {
        Event event = (id != null) ? eventService.findById(id).orElse(new Event()) : new Event();
        model.addAttribute("event", event);
        model.addAttribute("eventos", eventService.getAll());
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
