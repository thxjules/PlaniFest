package com.example.planifest.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.planifest.service.EventServiceImp;
import com.example.planifest.service.ReportService;
import com.example.planifest.service.SupplyServiceImp;
import com.example.planifest.service.TaskServiceImp;
import com.example.planifest.service.UserServiceImp;

@Controller
@RequestMapping("/admin")
public class AdminReportController {

    private final ReportService reportService;
    private final UserServiceImp empleadoService;
    private final EventServiceImp eventoService;
    private final TaskServiceImp tareaService;
    private final SupplyServiceImp supplyService; 

    public AdminReportController(
        ReportService reportService,
        UserServiceImp empleadoService,
        EventServiceImp eventoService,
        TaskServiceImp tareaService,
        SupplyServiceImp supplyService 
    ) {
        this.reportService = reportService;
        this.empleadoService = empleadoService;
        this.eventoService = eventoService;
        this.tareaService = tareaService;
        this.supplyService = supplyService;
    }

    @GetMapping("/report")
    public ResponseEntity<byte[]> generateAdminReport() throws Exception {
    Map<String, Object> data = new HashMap<>();
    data.put("totalEmpleados", empleadoService.countEmployees());
    data.put("totalEventos", eventoService.count());
    data.put("totalTareas", tareaService.count());
    data.put("totalInsumos", supplyService.count());

    // Logo exclusivo para reporte admin
    String logoAdmin = reportService.encodeImageToBase64("src/main/resources/static/images/dashboard/admin.png");
    data.put("logoBase64", logoAdmin);

    byte[] pdf = reportService.generatePdf("reportAdmin", data);

    return ResponseEntity.ok()
            .contentType(MediaType.APPLICATION_PDF)
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=admin-report.pdf")
            .body(pdf);
}
}