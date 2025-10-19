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

import com.example.planifest.Exceptions.SupplyDeletionException;
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
    public String listarSupplies(
        @RequestParam(name = "id", required= false) Long id, 
        @RequestParam(required = false) String nombre,
        @RequestParam(required = false) String lugarAlmacen,
        @RequestParam(required = false) Integer stockActual,
        Model model) {

            
        /* Explicacion de la logica del filtro */
        Supply supply = (id != null) ? supplyService.findById(id).orElse(new Supply()): new Supply();
         List<Supply> supplies = supplyService.filtrosMultiTabla(nombre, lugarAlmacen, stockActual);

        model.addAttribute("nombre", nombre);
        model.addAttribute("lugarAlmacen", lugarAlmacen);
        model.addAttribute("stockActual", stockActual);
        model.addAttribute("supply", supply);
        model.addAttribute("suministros", supplies);
        return "supplies";
    }

    /* Usan el mismo formulario (Por eso la condicional) */
    @PostMapping("/save")
    public String guardarSupply(@ModelAttribute Supply supply, Model model, RedirectAttributes redirectAttributes) {
        try {
            if (supply.getId() == null) {
                supplyService.create(supply);
                redirectAttributes.addFlashAttribute("successMessage", "El suministro se ha creado exitosamente.");
                /* Creacion de un suministro */
            } else {
                supplyService.update(supply);
                redirectAttributes.addFlashAttribute("successMessage", "El suministro ha sido actualizado correctamente.");/* Actualizacion */
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
    public String eliminarSupply(@PathVariable Long id, RedirectAttributes redirectAttributes) {
            try {
        supplyService.deleteById(id);
        redirectAttributes.addFlashAttribute("successMessage", "Suministro eliminado correctamente.");
    } catch (SupplyDeletionException e) {
        redirectAttributes.addFlashAttribute("error", e.getMessage());
    }
        return "redirect:/supplies-view";
    }

}
