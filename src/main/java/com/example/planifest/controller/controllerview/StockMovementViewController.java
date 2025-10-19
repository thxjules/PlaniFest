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
    public String listarStock(
        @RequestParam(name = "id", required=false)Long id,
        @RequestParam(required = false) Integer cantidad,
        @RequestParam(required = false) String tipo,
        @RequestParam(required = false) Long suministroId,
        Model model){

        StockMovement stockMovement = (id != null) ? stockMovementService.findById(id).orElse(new StockMovement()): new StockMovement();
        List<StockMovement> stockMovements = stockMovementService.filtrosMultitabla(cantidad, tipo, suministroId);

        model.addAttribute("cantidad", cantidad);
        model.addAttribute("tipo", tipo);
        model.addAttribute("stockMovement", stockMovement);
        model.addAttribute("stockMovements", stockMovements);
        model.addAttribute("suministroId", suministroId);
        model.addAttribute("supplies", supplyService.getAll());


        return "stockMovement";
    }

@PostMapping("/save")
public String guardarStock(@ModelAttribute StockMovement stockMovement, Model model, RedirectAttributes redirectAttributes) {
    try {
        if (stockMovement.getId() == null) {
            stockMovementService.create(stockMovement);
                        redirectAttributes.addFlashAttribute("successMessage", "El movimiento de stock ha sido creado correctamente.");
        } else {
            stockMovementService.update(stockMovement);
                       redirectAttributes.addFlashAttribute("successMessage", "El movimiento de Stock ha actualizado correctamente.");
        }
        return "redirect:/stock-view";
    } catch (RuntimeException ex) {
        model.addAttribute("stockMovement", stockMovement);
        model.addAttribute("stockMovements", stockMovementService.getAll());
        model.addAttribute("supplies", supplyService.getAll()); // ✅ nombre correcto
        model.addAttribute("error", ex.getMessage());

        return "stockMovement";
    }
}




    @GetMapping("/delete/{id}")
    public String eliminarStock(@PathVariable Long id, RedirectAttributes redirectAttributes) {
                try {
                    stockMovementService.deleteById(id);
            redirectAttributes.addFlashAttribute("successMessage", "Movimiento de stock eliminado correctamente.");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", "Error al eliminar el Movimiento: " + e.getMessage());
        }
        return "redirect:/stock-view";
    }

}
