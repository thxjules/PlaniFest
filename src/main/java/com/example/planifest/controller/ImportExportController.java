package com.example.planifest.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.planifest.service.ImportExportService;

@RestController
@RequestMapping("/api/import-export")
public class ImportExportController {

    private final ImportExportService importExportService;

    public ImportExportController(ImportExportService importExportService) {
        this.importExportService = importExportService;
    }

    // ================== Importación ==================
    @PostMapping("/{entity}/import")
    public ResponseEntity<List<String>> importData(
            @PathVariable String entity,
            @RequestParam("file") MultipartFile file
    ) {
        try {
            Class<?> entityClass = getEntityClass(entity);
            List<String> errors = importExportService.importData(file, entityClass);
            return ResponseEntity.ok(errors);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(List.of(e.getMessage()));
        }
    }

    

    // ================== Helper ==================
    private Class<?> getEntityClass(String entity) {
        return switch (entity.toLowerCase()) {
            case "user" -> com.example.planifest.entity.User.class;
            case "task" -> com.example.planifest.entity.Task.class;
            case "event" -> com.example.planifest.entity.Event.class;
            case "supply" -> com.example.planifest.entity.Supply.class;
            case "client" -> com.example.planifest.entity.Client.class;
            default -> throw new IllegalArgumentException("Entidad no soportada: " + entity);
        };
    }
}