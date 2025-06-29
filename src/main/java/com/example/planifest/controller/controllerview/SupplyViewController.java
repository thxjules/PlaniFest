package com.example.planifest.controller.controllerview;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.planifest.entity.Supply;
import com.example.planifest.service.EventServiceImp;
import com.example.planifest.service.StockMovementServiceImp;
import com.example.planifest.service.SupplyServiceImp;

@Controller
@RequestMapping("/supplies-view")
public class SupplyViewController {

    private final SupplyServiceImp supplyService;
    private final EventServiceImp eventService;
    private final StockMovementServiceImp StockMovementService;

    public SupplyViewController(SupplyServiceImp supplyService, EventServiceImp eventService, StockMovementServiceImp stockMovementService) {
        this.supplyService = supplyService;
        this.eventService = eventService;
        this.StockMovementService = stockMovementService;
    }

    /* Metodo para traer los Suministros a la vista */

    @GetMapping
    public String listarSupplies(Model model){
        model.addAttribute("supplies", supplyService.getAll() );
        model.addAttribute("events", eventService.getAll());
        return "supplies";
    }

    @GetMapping("/create")
    public String nuevoSupply(Model model){
        model.addAttribute("supply", new Supply());
        return "supplies";
    }

    @PostMapping("/save")
    public String guardarSupply(@ModelAttribute Supply supply, Model model){
        try {
            if(supply.getId() == null){
                supplyService.create(supply);
            } else{
                supplyService.update(supply);
            }
            return "redirect:/supply/List";
        } catch (RuntimeException ex){
        model.addAttribute("supply", supply);
        model.addAttribute("supplies", supplyService.getAll());
        model.addAttribute("stockMovements", StockMovementService.getAll());
        model.addAttribute("events", eventService.getAll());
        model.addAttribute("error", ex.getMessage());

        /* Representa la ruda del request  */
            return "redirect:/supplies-view";
        }
    }

    @GetMapping("/delete/{id}")
    public String eliminarSupply(@PathVariable Long id){
        supplyService.deleteById(id);
        return "redirect:/supplies-view";
    }








}
