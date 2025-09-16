package com.example.planifest.service.exportstrategy;

import java.io.ByteArrayOutputStream;
import java.io.StringWriter;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import com.example.planifest.entity.User;
import com.example.planifest.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class UserExportStrategy implements ExportStrategy<User> {

    private final UserRepository userRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public UserExportStrategy(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public String exportToJson(List<User> data) throws Exception {
        return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(data);
    }

    @Override
    public byte[] exportToCsv(List<User> data) throws Exception {
        StringWriter writer = new StringWriter();
        CSVPrinter printer = new CSVPrinter(writer, CSVFormat.DEFAULT.withHeader("username", "email", "role"));

        for (User u : data) {
            printer.printRecord(u.getUsername(), u.getEmail(), u.getRole());
        }

        printer.flush();
        return writer.toString().getBytes();
    }

    @Override
    public byte[] exportToExcel(List<User> data) throws Exception {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Users");

        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("Username");
        header.createCell(1).setCellValue("Email");
        header.createCell(2).setCellValue("Role");

        int rowNum = 1;
        for (User u : data) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(u.getUsername());
            row.createCell(1).setCellValue(u.getEmail());
            row.createCell(2).setCellValue(u.getRole().toString());
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        workbook.write(baos);
        workbook.close();
        return baos.toByteArray();
    }
}
