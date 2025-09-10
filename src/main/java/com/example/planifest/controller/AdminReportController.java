package com.example.planifest.controller;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

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
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.planifest.entity.Event;
import com.example.planifest.entity.User;
import com.example.planifest.service.EventServiceImp;
import com.example.planifest.service.SupplyServiceImp;
import com.example.planifest.service.TaskServiceImp;
import com.example.planifest.service.UserServiceImp;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Image;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import jakarta.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/admin/report")
public class AdminReportController {

    private final UserServiceImp empleadoService;
    private final EventServiceImp eventoService;
    private final TaskServiceImp tareaService;
    private final SupplyServiceImp supplyService;

    public AdminReportController(UserServiceImp empleadoService, EventServiceImp eventoService,
            TaskServiceImp tareaService, SupplyServiceImp supplyService) {
        this.empleadoService = empleadoService;
        this.eventoService = eventoService;
        this.tareaService = tareaService;
        this.supplyService = supplyService;
    }

    // ================= PDF COMPLETO =================
    @GetMapping("/pdf/all")
    public void downloadPdf(HttpServletResponse response) throws Exception {
        response.setContentType("application/pdf");
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=reporte-combinado.pdf");

        List<User> empleados = empleadoService.getEmployeesFilteredByPosition(null);
        List<Event> eventos = eventoService.getAll();

        long totalEmpleados = empleadoService.countEmployees();
        long totalEventos = eventos.size();
        long totalTareas = tareaService.count();
        long tareasPendientes = tareaService.getAll().stream()
                .filter(task -> task.getStatus().name().equalsIgnoreCase("PENDING")).count();
        long tareasCompletadas = totalTareas - tareasPendientes;
        long totalInsumos = supplyService.count();

        long empleadosAdmin = empleadoService.getAll().stream().filter(u -> u.getRole().name().equals("ADMIN")).count();
        long empleadosEmployee = empleadoService.getAll().stream().filter(u -> u.getRole().name().equals("EMPLOYEE"))
                .count();
        long empleadosOther = totalEmpleados - (empleadosAdmin + empleadosEmployee);

        long eventosFuturos = eventos.stream().filter(e -> e.getDate().isAfter(LocalDate.now())).count();
        long eventosRealizados = totalEventos - eventosFuturos;

        Document document = new Document();
        PdfWriter.getInstance(document, response.getOutputStream());
        document.open();

        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16);

        // ==== SECCIÓN 1: TABLA ADMIN ====
        Paragraph adminTitle = new Paragraph("Reporte Administrativo", titleFont);
        adminTitle.setAlignment(Element.ALIGN_CENTER);
        document.add(adminTitle);
        document.add(new Paragraph(" "));

        PdfPTable adminTable = new PdfPTable(2);
        adminTable.setWidthPercentage(100f);
        adminTable.addCell("Total Empleados");
        adminTable.addCell(String.valueOf(totalEmpleados));
        adminTable.addCell("Total Eventos");
        adminTable.addCell(String.valueOf(totalEventos));
        adminTable.addCell("Total Tareas");
        adminTable.addCell(String.valueOf(totalTareas));
        adminTable.addCell("Tareas Pendientes");
        adminTable.addCell(String.valueOf(tareasPendientes));
        adminTable.addCell("Total Suministros");
        adminTable.addCell(String.valueOf(totalInsumos));
        document.add(adminTable);

        // ==== SECCIÓN 2: LISTADO DE EMPLEADOS ====
        document.newPage();
        Paragraph empTitle = new Paragraph("Listado de Empleados", titleFont);
        empTitle.setAlignment(Element.ALIGN_CENTER);
        document.add(empTitle);
        document.add(new Paragraph(" "));

        PdfPTable empTable = new PdfPTable(5);
        empTable.setWidthPercentage(100f);
        Stream.of("Nombre", "Email", "Teléfono", "Rol", "Posición").forEach(header -> {
            PdfPCell cell = new PdfPCell(new Phrase(header));
            cell.setBackgroundColor(Color.LIGHT_GRAY);
            empTable.addCell(cell);
        });

        for (User e : empleados) {
            empTable.addCell(e.getUsername());
            empTable.addCell(e.getEmail());
            empTable.addCell(e.getPhoneNumber());
            empTable.addCell(e.getRole().name());
            empTable.addCell(e.getPosition() != null ? e.getPosition().getName() : "");
        }
        document.add(empTable);

        // ==== SECCIÓN 3: GRÁFICAS ====
        document.newPage();
        addChartToPdf(document, new DefaultPieDataset() {
            {
                setValue("Admin", empleadosAdmin);
                setValue("Empleado", empleadosEmployee);
                setValue("Otros", empleadosOther);
            }
        }, "Empleados por rol");

        addChartToPdf(document, new DefaultPieDataset() {
            {
                setValue("Pendientes", tareasPendientes);
                setValue("Completadas", tareasCompletadas);
            }
        }, "Tareas");

        DefaultCategoryDataset datasetEvent = new DefaultCategoryDataset();
        datasetEvent.addValue(eventosRealizados, "Eventos", "Realizados");
        datasetEvent.addValue(eventosFuturos, "Eventos", "Pendientes");
        addChartToPdf(document, datasetEvent, "Eventos");

        document.close();
    }

    private void addChartToPdf(Document document, Object dataset, String title) throws Exception {
        JFreeChart chart;
        if (dataset instanceof DefaultPieDataset pieDataset) {
            chart = ChartFactory.createPieChart(title, pieDataset, true, true, false);
        } else if (dataset instanceof DefaultCategoryDataset catDataset) {
            chart = ChartFactory.createBarChart(title, "Tipo", "Cantidad", catDataset);
        } else {
            return;
        }
        File chartFile = File.createTempFile(title.replace(" ", "_"), ".png");
        ChartUtils.saveChartAsPNG(chartFile, chart, 500, 400);
        Image chartImage = Image.getInstance(chartFile.getAbsolutePath());
        chartImage.setAlignment(Element.ALIGN_CENTER);
        document.add(chartImage);
        document.add(new Paragraph(" "));
    }

    // ================= EXCEL COMPLETO =================
   @GetMapping("/excel/all")
    public void downloadExcel(HttpServletResponse response) throws Exception {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=reporte-combinado.xlsx");

        List<User> empleados = empleadoService.getEmployeesFilteredByPosition(null);
        List<Event> eventos = eventoService.getAll();

        long totalEmpleados = empleadoService.countEmployees();
        long totalEventos = eventos.size();
        long totalTareas = tareaService.count();
        long tareasPendientes = tareaService.getAll().stream()
                .filter(task -> task.getStatus().name().equalsIgnoreCase("PENDING")).count();
        long tareasCompletadas = totalTareas - tareasPendientes;
        long totalInsumos = supplyService.count();

        long empleadosAdmin = empleadoService.getAll().stream().filter(u -> u.getRole().name().equals("ADMIN")).count();
        long empleadosEmployee = empleadoService.getAll().stream().filter(u -> u.getRole().name().equals("EMPLOYEE")).count();
        long empleadosOther = totalEmpleados - (empleadosAdmin + empleadosEmployee);

        long eventosFuturos = eventos.stream().filter(e -> e.getDate().isAfter(LocalDate.now())).count();
        long eventosRealizados = totalEventos - eventosFuturos;

        Workbook workbook = new XSSFWorkbook();

        // ==== HOJA 1: ADMIN ====
        Sheet adminSheet = workbook.createSheet("Admin");
        int rowNum = 0;
        Row header = adminSheet.createRow(rowNum++);
        header.createCell(0).setCellValue("Concepto");
        header.createCell(1).setCellValue("Cantidad");

        Map<String, Object> adminData = new HashMap<>();
        adminData.put("Total Empleados", totalEmpleados);
        adminData.put("Total Eventos", totalEventos);
        adminData.put("Total Tareas", totalTareas);
        adminData.put("Tareas Pendientes", tareasPendientes);
        adminData.put("Total Suministros", totalInsumos);

        for (Map.Entry<String, Object> entry : adminData.entrySet()) {
            Row row = adminSheet.createRow(rowNum++);
            row.createCell(0).setCellValue(entry.getKey());
            row.createCell(1).setCellValue(entry.getValue().toString());
        }

        // ==== HOJA 2: EMPLEADOS ====
        Sheet empSheet = workbook.createSheet("Empleados");
        Row empHeader = empSheet.createRow(0);
        String[] columns = { "Nombre", "Email", "Teléfono", "Rol", "Posición" };
        for (int i = 0; i < columns.length; i++) {
            empHeader.createCell(i).setCellValue(columns[i]);
        }

        int empRowNum = 1;
        for (User e : empleados) {
            Row row = empSheet.createRow(empRowNum++);
            row.createCell(0).setCellValue(e.getUsername());
            row.createCell(1).setCellValue(e.getEmail());
            row.createCell(2).setCellValue(e.getPhoneNumber());
            row.createCell(3).setCellValue(e.getRole().name());
            row.createCell(4).setCellValue(e.getPosition() != null ? e.getPosition().getName() : "");
        }

        // ==== HOJA 3: GRÁFICAS ====
        Sheet chartSheet = workbook.createSheet("Gráficas");
        int rowIndex = 0;

        // Generar imágenes de las gráficas
        byte[] empChartBytes = generateChartAsBytes(new DefaultPieDataset() {{
            setValue("Admin", empleadosAdmin);
            setValue("Empleado", empleadosEmployee);
            setValue("Otros", empleadosOther);
        }}, "Empleados por rol");

        byte[] taskChartBytes = generateChartAsBytes(new DefaultPieDataset() {{
            setValue("Pendientes", tareasPendientes);
            setValue("Completadas", tareasCompletadas);
        }}, "Tareas");

        DefaultCategoryDataset eventDataset = new DefaultCategoryDataset();
        eventDataset.addValue(eventosRealizados, "Eventos", "Realizados");
        eventDataset.addValue(eventosFuturos, "Eventos", "Pendientes");
        byte[] eventChartBytes = generateChartAsBytes(eventDataset, "Eventos");

        // Insertar las imágenes en la hoja
        insertImageInSheet(workbook, chartSheet, empChartBytes, rowIndex); rowIndex += 20;
        insertImageInSheet(workbook, chartSheet, taskChartBytes, rowIndex); rowIndex += 20;
        insertImageInSheet(workbook, chartSheet, eventChartBytes, rowIndex);

        workbook.write(response.getOutputStream());
        workbook.close();
    }

    private byte[] generateChartAsBytes(Object dataset, String title) throws Exception {
        JFreeChart chart;
        if (dataset instanceof DefaultPieDataset pieDataset) {
            chart = ChartFactory.createPieChart(title, pieDataset, true, true, false);
        } else if (dataset instanceof DefaultCategoryDataset catDataset) {
            chart = ChartFactory.createBarChart(title, "Tipo", "Cantidad", catDataset);
        } else {
            return null;
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ChartUtils.writeChartAsPNG(baos, chart, 500, 400);
        return baos.toByteArray();
    }

    private void insertImageInSheet(Workbook workbook, Sheet sheet, byte[] imageBytes, int rowIndex) {
        int pictureIdx = workbook.addPicture(imageBytes, Workbook.PICTURE_TYPE_PNG);
        XSSFClientAnchor anchor = new XSSFClientAnchor();
        anchor.setCol1(0);
        anchor.setRow1(rowIndex);
        anchor.setCol2(10);
        anchor.setRow2(rowIndex + 20);
        sheet.createDrawingPatriarch().createPicture(anchor, pictureIdx);
    }
}