package com.example.planifest.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.planifest.entity.StockMovement;
import com.example.planifest.service.StockMovementServiceImp;

@RestController
@RequestMapping("/stockMovements")
public class StockMovementController {

    private final StockMovementServiceImp stockMovementService;

    public StockMovementController(StockMovementServiceImp stockMovementService) {
        this.stockMovementService = stockMovementService;

    }

    @GetMapping
    public ResponseEntity<List<StockMovement>> getAllStockMovements() {
        return ResponseEntity.ok(stockMovementService.getAll());
    }

    @PostMapping
    public ResponseEntity<?> createStockMovement(@RequestBody StockMovement stockMovement) {
        stockMovementService.create(stockMovement);
        return ResponseEntity.status(201).build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateStockMovement(@PathVariable Long id, @RequestBody StockMovement stockMovement) {
        stockMovement.setId(id);
        stockMovementService.update(stockMovement);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteStockMovement(@PathVariable Long id) {
        stockMovementService.deleteById(id);
        return ResponseEntity.ok().build();
    }

}
