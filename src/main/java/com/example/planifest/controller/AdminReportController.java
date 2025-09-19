package com.example.planifest.controller;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFClientAnchor;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.planifest.service.EventServiceImp;
import com.example.planifest.service.ReportService;
import com.example.planifest.service.SupplyServiceImp;
import com.example.planifest.service.TaskServiceImp;
import com.example.planifest.service.UserServiceImp;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Image;
import com.lowagie.text.Paragraph;

import jakarta.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/admin/report")
public class AdminReportController {

    private final UserServiceImp empleadoService;
    private final EventServiceImp eventoService;
    private final TaskServiceImp tareaService;
    private final SupplyServiceImp supplyService;
    private final ReportService reportService;

    public AdminReportController(UserServiceImp empleadoService, EventServiceImp eventoService,
                                 TaskServiceImp tareaService, SupplyServiceImp supplyService,
                                 ReportService reportService) {
        this.empleadoService = empleadoService;
        this.eventoService = eventoService;
        this.tareaService = tareaService;
        this.supplyService = supplyService;
        this.reportService = reportService;
    }

    // ----- Método auxiliar para agregar gráficos al PDF -----
    private void addChartToPdf(Document document, Object dataset, String title) throws Exception {
        JFreeChart chart;
        if (dataset instanceof DefaultPieDataset pieDataset) {
            chart = ChartFactory.createPieChart(title, pieDataset, true, true, false);
        } else if (dataset instanceof DefaultCategoryDataset catDataset) {
            chart = ChartFactory.createBarChart(title, "Tipo", "Cantidad", catDataset);
        } else return;

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ChartUtils.writeChartAsPNG(baos, chart, 500, 400);
        Image chartImage = Image.getInstance(baos.toByteArray());
        chartImage.setAlignment(Element.ALIGN_CENTER);
        document.add(chartImage);
        document.add(new Paragraph(" "));
    }

    // ----- PDF completo del dashboard -----
  @GetMapping("/admin/report/pdf/full")
public ResponseEntity<byte[]> generarReporte() throws Exception {
    Map<String, Object> data = new HashMap<>();
    data.put("empleados", 12);
    data.put("eventos", 5);
    data.put("tareas", 20);
    data.put("pendientes", 7);
    data.put("insumos", 15);

    byte[] pdfBytes = reportService.generatePdf("Reporte Dashboard", data);

    return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=reporte_dashboard.pdf")
            .contentType(MediaType.APPLICATION_PDF)
            .body(pdfBytes);
}


    // ----- Excel del dashboard -----
    @PostMapping("/excel/full")
    public void downloadExcelFull(@RequestBody Map<String, Object> dashboardData, HttpServletResponse response) throws Exception {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=reporte_dashboard.xlsx");

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Dashboard");

        // --- TABLA RESUMEN ---
        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("Concepto");
        header.createCell(1).setCellValue("Cantidad");

        String[][] data = {
            {"Total Tareas", dashboardData.getOrDefault("totalTareas","0").toString()},
            {"Tareas Pendientes", dashboardData.getOrDefault("tareasPendientes","0").toString()},
            {"Total Empleados", dashboardData.getOrDefault("totalEmpleados","0").toString()},
            {"Total Suministros", dashboardData.getOrDefault("totalInsumos","0").toString()},
            {"Eventos Realizados", dashboardData.getOrDefault("eventosRealizados","0").toString()},
            {"Eventos Futuros", dashboardData.getOrDefault("eventosFuturos","0").toString()},
        };
        for(int i=0;i<data.length;i++){
            Row row = sheet.createRow(i+1);
            row.createCell(0).setCellValue(data[i][0]);
            row.createCell(1).setCellValue(data[i][1]);
        }

        // --- GRÁFICOS ---
        Map<String, String> charts = (Map<String, String>) dashboardData.get("charts");
        if(charts != null){
            int rowIndex = 10;
            for(String key : charts.keySet()){
                String base64 = charts.get(key).split(",")[1];
                byte[] bytes = java.util.Base64.getDecoder().decode(base64);
                int pictureIdx = workbook.addPicture(bytes, Workbook.PICTURE_TYPE_PNG);
                XSSFClientAnchor anchor = new XSSFClientAnchor();
                anchor.setCol1(0);
                anchor.setRow1(rowIndex);
                anchor.setCol2(10);
                anchor.setRow2(rowIndex + 20);
                sheet.createDrawingPatriarch().createPicture(anchor, pictureIdx);
                rowIndex += 25;
            }
        }

        workbook.write(response.getOutputStream());
        response.getOutputStream().flush();
        workbook.close();
    }
}
