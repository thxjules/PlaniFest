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
import com.example.planifest.service.ReportService;
import com.example.planifest.service.SupplyServiceImp;

@Controller
@RequestMapping("/admin/stock")
public class StockReportController {

    private final ReportService reportService;
    private final SupplyServiceImp supplyService;

    public StockReportController(ReportService reportService, SupplyServiceImp supplyService) {
        this.reportService = reportService;
        this.supplyService = supplyService;
    }
@GetMapping("/report")
public ResponseEntity<byte[]> generateStockReport() throws Exception {
    List<Supply> supplies = supplyService.getAll();

    Map<String, Object> data = new HashMap<>();
    data.put("supplies", supplies);

    String logoStock = reportService.encodeImageToBase64("src/main/resources/static/images/dashboard/stock.png");
    data.put("logoBase64", logoStock);

    byte[] pdf = reportService.generatePdf("reportStock", data);

    return ResponseEntity.ok()
            .contentType(MediaType.APPLICATION_PDF)
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=stock-report.pdf")
            .body(pdf);
    }
}