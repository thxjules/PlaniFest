package com.example.planifest.controller.controllerview;

import com.example.planifest.entity.Client;
import com.example.planifest.service.ClientServiceImp;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequestMapping("/clients-view")
public class ClientViewController {

    private final ClientServiceImp clientService;

    public ClientViewController(ClientServiceImp clientService) {
        this.clientService = clientService;
    }

    @GetMapping
    public String mostrarClientes(@RequestParam(name = "id", required = false) Long id, Model model) {
        Client client = new Client();
        if (id != null) {
            Optional<Client> optionalClient = clientService.getAll().stream()
                    .filter(c -> c.getId().equals(id))
                    .findFirst();
            if (optionalClient.isPresent()) {
                client = optionalClient.get();
            }
        }

        model.addAttribute("client", client);
        model.addAttribute("clientes", clientService.getAll());
        return "clients";
    }

    @PostMapping("/save")
    public String guardarCliente(@ModelAttribute Client client, Model model) {
        try {
            if (client.getId() == null) {
                clientService.create(client);
            } else {
                clientService.update(client);
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
    public String eliminarCliente(@PathVariable Long id) {
        clientService.deleteById(id);
        return "redirect:/clients-view";
    }
}
