package com.example.planifest.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.planifest.entity.Supply;
import com.example.planifest.service.StockMovementServiceImp;
import com.example.planifest.service.SupplyServiceImp;

@Controller
public class StockDashboardController {
    @Autowired
    private SupplyServiceImp supplyService;

    @Autowired
    private StockMovementServiceImp StockService;

    @GetMapping("/dashboard/stock")
    public String showInventoryDashboard(Model model) {

        long totalSupplies = supplyService.count();
        model.addAttribute("totalSupplies", totalSupplies);

        List<Supply> criticalSupplies = supplyService.getAll().stream()
                .filter(s -> s.getCurrentStock() < s.getMinStock())
                .collect(Collectors.toList());
        model.addAttribute("criticalSupplies", criticalSupplies);
        model.addAttribute("lowStockCount", criticalSupplies.size());

        model.addAttribute("activePage", "stock");
        return "/dashboard/stock";
    }
}