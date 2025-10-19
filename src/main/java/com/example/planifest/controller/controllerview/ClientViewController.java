package com.example.planifest.controller.controllerview;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.planifest.entity.Client;
import com.example.planifest.service.ClientServiceImp;
import com.example.planifest.service.EventServiceImp;

@Controller
@RequestMapping("/clients-view")
public class ClientViewController {

    private final ClientServiceImp clientService;
    private final EventServiceImp  eventoService;

    public ClientViewController(ClientServiceImp clientService, EventServiceImp  eventoService) {
        this.clientService = clientService;
        this.eventoService = eventoService;
    }

    @GetMapping
    public String mostrarClientes(
        @RequestParam(name = "id", required = false) Long id,
        @RequestParam(required = false) String nombre,
        @RequestParam(required = false) String email,
        Model model) {
        Client client = (id != null) ? clientService.findById(id).orElse(new Client()):new Client();

        List<Client> clients = clientService.filtroNombreEmail(nombre, email);

        model.addAttribute("nombre", nombre);
        model.addAttribute("email", email);
        model.addAttribute("client", client);
        model.addAttribute("clientes", clients);
        return "clients";
    }

    @PostMapping("/save")
    public String guardarCliente(@ModelAttribute Client client, Model model, RedirectAttributes redirectAttributes) {
        try {
            if (client.getId() == null) {
                clientService.create(client);
                redirectAttributes.addFlashAttribute("successMessage", "Cliente creado exitosamente.");
            } else {
                clientService.update(client);
                redirectAttributes.addFlashAttribute("successMessage", "Cliente actualizado exitosamente.");
            }
            return "redirect:/clients-view";
        } catch (RuntimeException e) {
            model.addAttribute("client", client);
            model.addAttribute("clientes", clientService.getAll());
            model.addAttribute("error", e.getMessage());
            return "clients";
        }
    }

@GetMapping("/delete/{id}")
public String eliminarCliente(@PathVariable Long id, Model model) {
    if (!eventoService.findByClientId(id).isEmpty()) {
        model.addAttribute("client", new Client()); // ✅ Agregar esto
        model.addAttribute("clientes", clientService.getAll());
        model.addAttribute("error", "No se puede eliminar el cliente porque tiene eventos asociados.");
        return "clients";
    }

    clientService.deleteById(id);
    return "redirect:/clients-view";
}

}
