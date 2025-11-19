package com.example.planifest.controller.controllerview;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.example.planifest.entity.Client;
import com.example.planifest.entity.Event;
import com.example.planifest.entity.PlaniService;
import com.example.planifest.entity.StockMovement;
import com.example.planifest.entity.Supply;
import com.example.planifest.entity.Task;
import com.example.planifest.entity.User;
import com.example.planifest.service.TrashService;

@Controller
public class TrashViewController {


    private final TrashService trashService;

    public TrashViewController(TrashService trashService){
        this.trashService = trashService;
    }

    /* Muestra los registros eliminados  */
    @GetMapping("/trash/{entity}")
    public String trash(@PathVariable String entity, Model model){
        List<?> items;
        switch(entity) {
            case "client": items = trashService.listDeleted(Client.class); break;

            case "event": items = trashService.listDeleted(Event.class); break;

            case "task": items = trashService.listDeleted(Task.class); break;

            case "planiService": items = trashService.listDeleted(PlaniService.class); break;

            case "stockMovement":items = trashService.listDeleted(StockMovement.class); break;

            case "supply": items = trashService.listDeleted(Supply.class); break;

            case "user": items = trashService.listDeleted(User.class); break;

          
            default: items = List.of();
        }
        model.addAttribute("entityType", entity);
        model.addAttribute("deletedItems", items);
        return "trash"; /* Nombre de la vista */
    }

    @PostMapping("/trash/{entity}/{id}/restore")
    public String restore(@PathVariable String entity, @PathVariable Long id) {
        switch(entity) {
            case "client": trashService.restore(Client.class, id); break;

            case "event": trashService.restore(Event.class, id); break;

            case "task": trashService.restore(Task.class, id); break;

            case "planiService": trashService.restore(PlaniService.class, id);break;

            case "stockMovement": trashService.restore(StockMovement.class, id);break;

            case "supply": trashService.restore(Supply.class, id); break;

            case "user": trashService.restore(User.class, id); break;
        }
        return "redirect:/trash/" + entity; /* ruta de la vista */
    }


}
