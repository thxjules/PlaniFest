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

import com.example.planifest.entity.PlaniService;
import com.example.planifest.service.PlaniServiceImp;

@RestController
@RequestMapping("/api/services")
public class PlaniServiceController {

    private final PlaniServiceImp planiService;

    public PlaniServiceController(PlaniServiceImp planiService) {
        this.planiService = planiService;
    }

    @GetMapping
    public ResponseEntity<List<PlaniService>> getAllServices() {
        return ResponseEntity.ok(planiService.getAll());
    }

    @PostMapping
    public ResponseEntity<?> createService(@RequestBody PlaniService service) {
        planiService.create(service);
        return ResponseEntity.status(201).build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateService(@PathVariable Long id, @RequestBody PlaniService service) {
        service.setServiceId(id);
        planiService.update(service);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteService(@PathVariable Long id) {
        planiService.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
