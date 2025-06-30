package com.example.planifest.controller.controllerview;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.planifest.entity.Supply;
import com.example.planifest.service.SupplyServiceImp;

@Controller
@RequestMapping("/supplies-view")
public class SupplyViewController {

    private final SupplyServiceImp supplyService;

    public SupplyViewController(SupplyServiceImp supplyService) {
        this.supplyService = supplyService;
    }

    /* Metodo para traer los Suministros a la vista */

    @GetMapping
    public String listarSupplies(@RequestParam(name = "id", required= false) Long id, Model model) {
        Supply supply = (id != null) ? supplyService.findById(id).orElse(new Supply()): new Supply();
        model.addAttribute("supply", supply);
        model.addAttribute("suministros", supplyService.getAll());
        return "supplies";
    }

    /* Usan el mismo formulario (Por eso la condicional) */
    @PostMapping("/save")
    public String guardarSupply(@ModelAttribute Supply supply, Model model) {
        try {
            if (supply.getId() == null) {
                supplyService.create(supply);/* Creacion de un suministro */
            } else {
                supplyService.update(supply);/* Actualizacion */
            }
            return "redirect:/supplies-view";
        } catch (RuntimeException ex) {
            model.addAttribute("supply", supply);
            model.addAttribute("supplies", supplyService.getAll());
            model.addAttribute("error", ex.getMessage());
            /* Vista de suministros*/
            return "supplies";
        }
    }

    @GetMapping("/delete/{id}")
    public String eliminarSupply(@PathVariable Long id) {
        supplyService.deleteById(id);
        return "redirect:/supplies-view";
    }

}
