package com.example.planifest.service.exportstrategy;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import com.example.planifest.entity.Task;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class TaskExportStrategy implements ExportStrategy<Task> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String exportToJson(List<Task> data) throws Exception {
        // Convierte lista de tareas a JSON
        return objectMapper.writeValueAsString(data);
    }

    @Override
    public byte[] exportToCsv(List<Task> data) throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        CSVPrinter printer = new CSVPrinter(new PrintWriter(out),
                CSVFormat.DEFAULT.withHeader("ID", "Name", "Description", "Date", "Status", "EventId", "UserId"));

        for (Task t : data) {
            printer.printRecord(
                    t.getId(),
                    t.getName(),
                    t.getDescription(),
                    t.getDate(),
                    t.getStatus(),
                    t.getEvent() != null ? t.getEvent().getId() : null,
                    t.getUser() != null ? t.getUser().getId() : null
            );
        }
        printer.flush();
        return out.toByteArray();
    }

    @Override
    public byte[] exportToExcel(List<Task> data) throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Tasks");

        // Encabezados
        Row header = sheet.createRow(0);
        String[] columns = {"ID", "Name", "Description", "Date", "Status", "EventId", "UserId"};
        for (int i = 0; i < columns.length; i++) {
            header.createCell(i).setCellValue(columns[i]);
        }

        // Datos
        int rowIdx = 1;
        for (Task t : data) {
            Row row = sheet.createRow(rowIdx++);
            row.createCell(0).setCellValue(t.getId());
            row.createCell(1).setCellValue(t.getName());
            row.createCell(2).setCellValue(t.getDescription());
            row.createCell(3).setCellValue(t.getDate() != null ? t.getDate().toString() : "");
            row.createCell(4).setCellValue(t.getStatus() != null ? t.getStatus().toString() : "");
            row.createCell(5).setCellValue(t.getEvent() != null ? t.getEvent().getId() : -1);
            row.createCell(6).setCellValue(t.getUser() != null ? t.getUser().getId() : -1);
        }

        workbook.write(out);
        workbook.close();
        return out.toByteArray();
    }
}
