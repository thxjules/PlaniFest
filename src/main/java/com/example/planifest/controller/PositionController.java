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

import com.example.planifest.entity.Position;
import com.example.planifest.service.PositionServiceImpl;

@RestController
@RequestMapping("/position")
public class PositionController {
    private final PositionServiceImpl positionService;

    public PositionController(PositionServiceImpl positionService) {
        this.positionService = positionService;
    }

    @GetMapping
    public ResponseEntity<List<Position>> getAllEvents() {
        return ResponseEntity.ok(positionService.getAll()); // status 200 + lista de eventos
    }

    @PostMapping
    public ResponseEntity<?> createPosition(@RequestBody Position position) {
        positionService.create(position);
        return ResponseEntity.status(201).build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updatePosition(@PathVariable Long id, @RequestBody Position position) {
        position.setId(id);
        positionService.update(position);
        return ResponseEntity.noContent().build(); // status 204 (sin contenido)
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> detelePosition(@PathVariable Long id) {
        positionService.deleteById(id);
        return ResponseEntity.ok().build(); // status 200 OK
    }

}
