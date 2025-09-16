package com.example.planifest.controller;

import com.example.planifest.service.ImportExportService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

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

    // ================== Exportación ==================
    @GetMapping("/{entity}/export/json")
    public ResponseEntity<String> exportJson(@PathVariable String entity) {
        try {
            Class<?> entityClass = getEntityClass(entity);
            String json = importExportService.exportJson(entityClass);
            return ResponseEntity.ok(json);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{entity}/export/csv")
    public ResponseEntity<byte[]> exportCsv(@PathVariable String entity) {
        try {
            Class<?> entityClass = getEntityClass(entity);
            byte[] data = importExportService.exportCsv(entityClass);
            return ResponseEntity.ok()
                    .header("Content-Disposition", "attachment; filename=" + entity + ".csv")
                    .body(data);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @GetMapping("/{entity}/export/excel")
    public ResponseEntity<byte[]> exportExcel(@PathVariable String entity) {
        try {
            Class<?> entityClass = getEntityClass(entity);
            byte[] data = importExportService.exportExcel(entityClass);
            return ResponseEntity.ok()
                    .header("Content-Disposition", "attachment; filename=" + entity + ".xlsx")
                    .body(data);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
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