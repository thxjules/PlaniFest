package com.example.planifest.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.planifest.service.ChartService;
import com.example.planifest.service.EventServiceImp;
import com.example.planifest.service.MetricService;
import com.example.planifest.service.ReportAIService;
import com.example.planifest.service.ReportService;
import com.example.planifest.service.SupplyServiceImp;
import com.example.planifest.service.TaskServiceImp;
import com.example.planifest.service.UserServiceImp;

@Controller
@RequestMapping("/admin")
public class AdminReportController {

    private final ReportService reportService;
    private final UserServiceImp userService;
    private final EventServiceImp eventoService;
    private final TaskServiceImp tareaService;
    private final SupplyServiceImp supplyService;
    private final ChartService chartService;
    private final MetricService metricService;
    private final ReportAIService reportAIService;

    public AdminReportController(
            ReportService reportService,
            UserServiceImp userService,
            EventServiceImp eventoService,
            TaskServiceImp tareaService,
            SupplyServiceImp supplyService,
            ChartService chartService,
            MetricService metricService,
            ReportAIService reportAIService
    ) {
        this.reportService = reportService;
        this.userService = userService;
        this.eventoService = eventoService;
        this.tareaService = tareaService;
        this.supplyService = supplyService;
        this.chartService = chartService;
        this.metricService = metricService;
        this.reportAIService = reportAIService;
    }

    @GetMapping("/report")
    public ResponseEntity<byte[]> generateAdminReport() throws Exception {

        Map<String, Object> data = new HashMap<>();

        // --- MÉTRICAS AUTOMÁTICAS ---
        Map<String, Object> metricas = metricService.generarMetricas();
        data.putAll(metricas);

        long emp = (long) metricas.get("totalEmpleados");
        long eve = (long) metricas.get("totalEventos");
        long tar = (long) metricas.get("totalTareas");
        long ins = (long) metricas.get("totalInsumos");

        // --- RESUMEN EJECUTIVO AUTOMÁTICO ---
        data.put("resumenEjecutivo", reportAIService.generarResumen(emp, eve, tar, ins));

        // --- RECOMENDACIONES AUTOMÁTICAS ---
        data.put("recomendaciones", reportAIService.generarRecomendaciones(emp, eve, tar, ins));

        // Logo
        String logoAdmin = reportService.encodeImageToBase64(
        "src/main/resources/static/images/planifestblanco.png"
        );
        data.put("logoBase64", logoAdmin);


        // Gráficos
        data.put("graficoAdmin", chartService.generarGraficoAdministracion(emp, eve, tar, ins));

        // PDF
        byte[] pdf = reportService.generatePdf("reportAdmin", data);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=admin-report.pdf")
                .body(pdf);
    }
}
