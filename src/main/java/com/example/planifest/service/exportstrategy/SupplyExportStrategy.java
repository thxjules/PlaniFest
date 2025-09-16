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

import com.example.planifest.entity.Supply;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class SupplyExportStrategy implements ExportStrategy<Supply> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String exportToJson(List<Supply> data) throws Exception {
        // Convierte la lista de Supply a JSON
        return objectMapper.writeValueAsString(data);
    }

    @Override
    public byte[] exportToCsv(List<Supply> data) throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        CSVPrinter printer = new CSVPrinter(new PrintWriter(out),
                CSVFormat.DEFAULT.withHeader(
                        "ID", "Name", "Type", "Description", "StorageLocation",
                        "Status", "CurrentStock", "MinStock", "MaxStock", "PackagingUnit"));

        for (Supply s : data) {
            printer.printRecord(
                    s.getId(),
                    s.getName(),
                    s.getSupplyType(),
                    s.getDescription(),
                    s.getStorageLocation(),
                    s.getStatus(),
                    s.getCurrentStock(),
                    s.getMinStock(),
                    s.getMaxStock(),
                    s.getPackagingUnit()
            );
        }
        printer.flush();
        return out.toByteArray();
    }

    @Override
    public byte[] exportToExcel(List<Supply> data) throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Supplies");

        // Crear encabezados
        Row header = sheet.createRow(0);
        String[] columns = {
                "ID", "Name", "Type", "Description", "StorageLocation",
                "Status", "CurrentStock", "MinStock", "MaxStock", "PackagingUnit"
        };
        for (int i = 0; i < columns.length; i++) {
            header.createCell(i).setCellValue(columns[i]);
        }

        // Rellenar filas
        int rowIdx = 1;
        for (Supply s : data) {
            Row row = sheet.createRow(rowIdx++);
            row.createCell(0).setCellValue(s.getId());
            row.createCell(1).setCellValue(s.getName());
            row.createCell(2).setCellValue(s.getSupplyType());
            row.createCell(3).setCellValue(s.getDescription());
            row.createCell(4).setCellValue(s.getStorageLocation());
            row.createCell(5).setCellValue(s.getStatus().toString());
            row.createCell(6).setCellValue(s.getCurrentStock());
            row.createCell(7).setCellValue(s.getMinStock());
            row.createCell(8).setCellValue(s.getMaxStock());
            row.createCell(9).setCellValue(s.getPackagingUnit());
        }

        workbook.write(out);
        workbook.close();
        return out.toByteArray();
    }
}
