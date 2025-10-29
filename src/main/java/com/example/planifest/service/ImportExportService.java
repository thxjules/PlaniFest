package com.example.planifest.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.planifest.service.importstrategy.ImportStrategy;
import com.example.planifest.service.importstrategy.ImportStrategyFactory;

@Service
public class ImportExportService {

    private final ImportStrategyFactory importFactory;


    public ImportExportService(ImportStrategyFactory importFactory, ImportStrategyFactory importFactory2) {
        this.importFactory = importFactory;
        
    }

    // ================== Importación ==================
    public List<String> importData(MultipartFile file, Class<?> entityClass) {
        ImportStrategy<?> strategy = importFactory.getStrategy(entityClass);
        List<String> errores = strategy.validate(file); // validar primero
        if (errores.isEmpty()) {
            strategy.saveAll(file); // si no hay errores, guardar todo
        }
        return errores;
    }

    
}
