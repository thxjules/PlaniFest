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

import com.example.planifest.entity.Client;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class ClientExportStrategy implements ExportStrategy<Client> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String exportToJson(List<Client> data) throws Exception {
        return objectMapper.writeValueAsString(data);
    }

    @Override
    public byte[] exportToCsv(List<Client> data) throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (PrintWriter writer = new PrintWriter(out);
             CSVPrinter printer = new CSVPrinter(writer,
                     CSVFormat.DEFAULT.withHeader("ID", "Name", "Email", "Phone"))) {
            for (Client c : data) {
                printer.printRecord(
                        c.getId(),
                        c.getName(),
                        c.getEmail(),
                        c.getPhone()
                );
            }
        }
        return out.toByteArray();
    }

    @Override
    public byte[] exportToExcel(List<Client> data) throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Clients");

            Row header = sheet.createRow(0);
            String[] columns = {"ID", "Name", "Email", "Phone"};
            for (int i = 0; i < columns.length; i++) {
                header.createCell(i).setCellValue(columns[i]);
            }

            int rowIdx = 1;
            for (Client c : data) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(c.getId());
                row.createCell(1).setCellValue(c.getName());
                row.createCell(2).setCellValue(c.getEmail());
                row.createCell(3).setCellValue(c.getPhone() != null ? c.getPhone() : "");
            }

            workbook.write(out);
        }
        return out.toByteArray();
    }
}
