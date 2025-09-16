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

import com.example.planifest.entity.Event;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

@Service
public class EventExportStrategy implements ExportStrategy<Event> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String exportToJson(List<Event> data) throws Exception {
        return objectMapper.writeValueAsString(data);
    }

    @Override
    public byte[] exportToCsv(List<Event> data) throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (PrintWriter writer = new PrintWriter(out);
             CSVPrinter printer = new CSVPrinter(writer, CSVFormat.DEFAULT
                     .withHeader("ID", "Name", "Description", "Date", "StartTime", "EndTime", "GuestCount", "Location", "Status"))) {

            for (Event e : data) {
                printer.printRecord(
                        e.getId(),
                        e.getEventName(),
                        e.getDescription(),
                        e.getDate(),
                        e.getStartTime(),
                        e.getEndTime(),
                        e.getGuestCount(),
                        e.getLocation(),
                        e.getStatus()
                );
            }
        }
        return out.toByteArray();
    }

    @Override
    public byte[] exportToExcel(List<Event> data) throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Events");

        // Header
        Row header = sheet.createRow(0);
        String[] columns = {"ID", "Name", "Description", "Date", "StartTime", "EndTime", "GuestCount", "Location", "Status"};
        for (int i = 0; i < columns.length; i++) {
            header.createCell(i).setCellValue(columns[i]);
        }

        // Data
        int rowIdx = 1;
        for (Event e : data) {
            Row row = sheet.createRow(rowIdx++);
            row.createCell(0).setCellValue(e.getId());
            row.createCell(1).setCellValue(e.getEventName());
            row.createCell(2).setCellValue(e.getDescription());
            row.createCell(3).setCellValue(e.getDate().toString());
            row.createCell(4).setCellValue(e.getStartTime().toString());
            row.createCell(5).setCellValue(e.getEndTime().toString());
            row.createCell(6).setCellValue(e.getGuestCount());
            row.createCell(7).setCellValue(e.getLocation());
            row.createCell(8).setCellValue(e.getStatus().toString());
        }

        workbook.write(out);
        workbook.close();
        return out.toByteArray();
    }
}
