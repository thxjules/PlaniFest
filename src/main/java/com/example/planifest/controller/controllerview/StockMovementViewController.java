package com.example.planifest.controller.controllerview;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.planifest.entity.StockMovement;
import com.example.planifest.service.StockMovementServiceImp;
import com.example.planifest.service.SupplyServiceImp;

@Controller
@RequestMapping("/stock-view")
public class StockMovementViewController {

    private final StockMovementServiceImp stockMovementService;
    private final SupplyServiceImp supplyService;

    public StockMovementViewController(StockMovementServiceImp stockMovementService, SupplyServiceImp supplyService) {
        this.stockMovementService = stockMovementService;
        this.supplyService = supplyService;
    }

    @GetMapping
    public String listarStock(Model model) {
        model.addAttribute("stockMovements", stockMovementService.getAll());
        return "stockMovement/list";
    }

    @GetMapping("/create")
    public String nuevoStock(Model model) {
        model.addAttribute("stockMovement", new StockMovement());
        return "stockMovement/formulario";
    }

    @GetMapping("/edit/{id}")
    public String editarStock(@PathVariable Long id, Model model) {
        StockMovement stockMovement = stockMovementService.getById(id);
        model.addAttribute("stockMovements", stockMovement);
        return "stockMovement/formulario";

    }

    @PostMapping("save")
    public String guardarStock(@ModelAttribute StockMovement stockMovement) {
        if (stockMovement.getId() == null) {
            stockMovementService.create(stockMovement);
        } else {
            stockMovementService.update(stockMovement);
        }
        return "redirect:/stockMovents";

    }

    @GetMapping("/delete/{id}")
    public String eliminarStock(@PathVariable Long id) {
        stockMovementService.deleteById(id);
        return "redirect:/stockMovement";
    }

}
