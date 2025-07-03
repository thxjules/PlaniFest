package com.example.planifest.controller.controllerview;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.planifest.entity.Supply;
import com.example.planifest.service.SupplyServiceImp;

@Controller
@RequestMapping("/supply-admin")
public class supplyAdminViewController {

     private final SupplyServiceImp supplyService;

    public supplyAdminViewController(SupplyServiceImp supplyService) {
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
        return "supplies-admin";
    }
}
