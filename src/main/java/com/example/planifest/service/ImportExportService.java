package com.example.planifest.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.planifest.service.exportstrategy.ExportStrategy;
import com.example.planifest.service.exportstrategy.ExportStrategyFactory;
import com.example.planifest.service.importstrategy.ImportStrategy;
import com.example.planifest.service.importstrategy.ImportStrategyFactory;

@Service
public class ImportExportService {

    private final ImportStrategyFactory importFactory;
    private final ExportStrategyFactory exportFactory;

    public ImportExportService(ImportStrategyFactory importFactory, ExportStrategyFactory exportFactory) {
        this.importFactory = importFactory;
        this.exportFactory = exportFactory;
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

    // ================== Exportación ==================
    @SuppressWarnings("unchecked")
    public String exportJson(Class<?> entityClass) throws Exception {
        ExportStrategy strategy = exportFactory.getStrategy(entityClass);
        List<?> data = exportFactory.getData(entityClass);
        return strategy.exportToJson(data);
    }

    @SuppressWarnings("unchecked")
    public byte[] exportCsv(Class<?> entityClass) throws Exception {
        ExportStrategy strategy = exportFactory.getStrategy(entityClass);
        List<?> data = exportFactory.getData(entityClass);
        return strategy.exportToCsv(data);
    }

    @SuppressWarnings("unchecked")
    public byte[] exportExcel(Class<?> entityClass) throws Exception {
        ExportStrategy strategy = exportFactory.getStrategy(entityClass);
        List<?> data = exportFactory.getData(entityClass);
        return strategy.exportToExcel(data);
    }
}
