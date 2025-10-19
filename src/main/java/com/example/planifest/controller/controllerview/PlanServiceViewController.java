package com.example.planifest.controller.controllerview;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.planifest.entity.PlaniService;
import com.example.planifest.service.PlaniServiceImp;

@Controller
@RequestMapping("/planservices")
public class PlanServiceViewController {

    @Autowired
    private PlaniServiceImp planiServiceImp;

    @GetMapping
    public String listarServicios(Model model,
            @RequestParam(required = false) String filtroNombre,
            @RequestParam(required = false) String filtroTipo) {

        List<PlaniService> servicios = planiServiceImp.getAll();

        if (filtroNombre != null && !filtroNombre.isBlank()) {
            servicios = servicios.stream()
                    .filter(s -> s.getName().toLowerCase().contains(filtroNombre.toLowerCase()))
                    .toList();
        }

        if (filtroTipo != null && !filtroTipo.isBlank()) {
            servicios = servicios.stream()
                    .filter(s -> s.getType() != null && s.getType().toLowerCase().contains(filtroTipo.toLowerCase()))
                    .toList();
        }

        model.addAttribute("servicios", servicios);
        model.addAttribute("nuevoServicio", new PlaniService());
        return "planiService";
    }

    @PostMapping("/create")
    public String crearServicio(@ModelAttribute("nuevoServicio") PlaniService servicio, RedirectAttributes redirectAttributes) {
        planiServiceImp.create(servicio);
                    redirectAttributes.addFlashAttribute("successMessage", "El Servicio se ha creado exitosamente.");
        return "redirect:/planservices";
    }

    @PostMapping("/update")
    public String actualizarServicio(@ModelAttribute PlaniService servicio, RedirectAttributes redirectAttributes) {
        planiServiceImp.update(servicio);
                    redirectAttributes.addFlashAttribute("successMessage", "El Servicio se ha Actualizado exitosamente.");
        return "redirect:/planservices";
    }

    @PostMapping("/delete/{serviceId}")
    public String eliminarServicio(@PathVariable Long serviceId) {
        planiServiceImp.deleteById(serviceId);
        return "redirect:/planservices";
    }

}
