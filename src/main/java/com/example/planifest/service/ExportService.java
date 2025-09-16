package com.example.planifest.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.planifest.service.exportstrategy.ExportStrategy;
import com.example.planifest.service.exportstrategy.ExportStrategyFactory;

@Service
public class ExportService {

    private final ExportStrategyFactory factory;

    public ExportService(ExportStrategyFactory factory) {
        this.factory = factory;
    }

    public String exportToJson(List<?> data, Class<?> entityClass) throws Exception {
        ExportStrategy strategy = factory.getStrategy(entityClass);
        return strategy.exportToJson(data);
    }

    public byte[] exportToCsv(List<?> data, Class<?> entityClass) throws Exception {
        ExportStrategy strategy = factory.getStrategy(entityClass);
        return strategy.exportToCsv(data);
    }

    public byte[] exportToExcel(List<?> data, Class<?> entityClass) throws Exception {
        ExportStrategy strategy = factory.getStrategy(entityClass);
        return strategy.exportToExcel(data);
    }
}

