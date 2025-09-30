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
import com.example.planifest.service.ReportService;
import com.example.planifest.service.SupplyServiceImp;
import com.example.planifest.service.TaskServiceImp;
import com.example.planifest.service.UserServiceImp;

@Controller
@RequestMapping("/admin")
public class AdminReportController {

    private final ReportService reportService;
    private final UserServiceImp UserService;
    private final EventServiceImp eventoService;
    private final TaskServiceImp tareaService;
    private final SupplyServiceImp supplyService; 
    private final ChartService chartService;


    public AdminReportController(
        ReportService reportService,
        UserServiceImp UserService,
        EventServiceImp eventoService,
        TaskServiceImp tareaService,
        SupplyServiceImp supplyService,
        ChartService chartService
    ) {
        this.reportService = reportService;
        this.UserService = UserService;
        this.eventoService = eventoService;
        this.tareaService = tareaService;
        this.supplyService = supplyService;
        this.chartService = chartService;

    }
    @GetMapping("/report")
public ResponseEntity<byte[]> generateAdminReport() throws Exception {
    Map<String, Object> data = new HashMap<>();

    // Conteos como long para evitar pérdida de información
    long totalEmpleados = UserService.countEmployees();
    long totalEventos = eventoService.count();
    long totalTareas = tareaService.count();
    long totalInsumos = supplyService.count();

    // Guardar en el mapa de datos
    data.put("totalEmpleados", totalEmpleados);
    data.put("totalEventos", totalEventos);
    data.put("totalTareas", totalTareas);
    data.put("totalInsumos", totalInsumos);

    // Logo exclusivo para reporte admin
    String logoAdmin = reportService.encodeImageToBase64(
        "src/main/resources/static/images/dashboard/logoplanifest.png"
    );
    data.put("logoBase64", logoAdmin);

    // Generar gráfico de barras para Administración
    String graficoAdmin = chartService.generarGraficoAdministracion(
        totalEmpleados, totalEventos, totalTareas, totalInsumos
    );
    data.put("graficoAdmin", graficoAdmin); // este es el que espera la plantilla

    // Generar PDF usando la plantilla Thymeleaf
    byte[] pdf = reportService.generatePdf("reportAdmin", data);

    return ResponseEntity.ok()
            .contentType(MediaType.APPLICATION_PDF)
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=admin-report.pdf")
            .body(pdf);
    }
}
