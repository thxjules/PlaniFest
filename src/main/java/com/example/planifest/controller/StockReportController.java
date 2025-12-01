package com.example.planifest.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.planifest.entity.Supply;
import com.example.planifest.service.ChartService;
import com.example.planifest.service.ReportService;
import com.example.planifest.service.SupplyServiceImp;

@Controller
@RequestMapping("/admin/stock")
public class StockReportController {

    private final ReportService reportService;
    private final SupplyServiceImp supplyService;
    private final ChartService chartService;

    public StockReportController(
        ReportService reportService,
        SupplyServiceImp supplyService,
        ChartService chartService
    ) {
        this.reportService = reportService;
        this.supplyService = supplyService;
        this.chartService = chartService;
    }

    @GetMapping("/report")
    public ResponseEntity<byte[]> generateStockReport() throws Exception {
        List<Supply> supplies = supplyService.getAll();

        Map<String, Object> data = new HashMap<>();
        data.put("supplies", supplies);

        String logoStock = reportService.encodeImageToBase64(        "src/main/resources/static/images/planifestBlanco.png");
        data.put("logoBase64", logoStock);

        Map<String, Integer> stockPorTipo = supplyService.obtenerStockAgrupadoPorTipo();

        // Generar gráfico de pastel
        String chartBase64 = chartService.generarGraficoInventario(stockPorTipo);
        data.put("chartBase64", chartBase64);

        // Gráfico comparativo de stock actual vs mínimo y máximo
        String chartBarras = chartService.generarGraficoStockMinimo(supplies);
        data.put("chartBarras", chartBarras);


        // Generar PDF usando plantilla reportStock.html
        byte[] pdf = reportService.generatePdf("reportStock", data);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=stock-report.pdf")
                .body(pdf);
    }
}
