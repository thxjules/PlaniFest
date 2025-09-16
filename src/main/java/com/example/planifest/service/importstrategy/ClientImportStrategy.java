package com.example.planifest.service.importstrategy;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.planifest.entity.Client;
import com.example.planifest.repository.ClientRepository;

@Service
public class ClientImportStrategy implements ImportStrategy<Client> {

    private final ClientRepository clientRepository;

    public ClientImportStrategy(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    @Override
    public List<String> validate(MultipartFile file) {
        return importFile(file, true);
    }

    @Override
    public void saveAll(MultipartFile file) {
        importFile(file, false);
    }

    private List<String> importFile(MultipartFile file, boolean soloValidar) {
        List<String> errores = new ArrayList<>();

        try {
            String filename = file.getOriginalFilename();
            if (filename == null) {
                errores.add("Archivo sin nombre");
                return errores;
            }

            if (filename.endsWith(".csv")) {
                errores.addAll(importCSV(file, soloValidar));
            } else if (filename.endsWith(".xlsx")) {
                errores.addAll(importExcel(file, soloValidar));
            } else {
                errores.add("Formato no soportado. Solo CSV o XLSX.");
            }

        } catch (Exception e) {
            errores.add("Error procesando archivo: " + e.getMessage());
        }

        return errores;
    }

    // ========================
    // Import CSV
    // ========================
    private List<String> importCSV(MultipartFile file, boolean soloValidar) {
        List<String> errores = new ArrayList<>();
        List<Client> clients = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {

            String headerLine = reader.readLine();
            if (headerLine == null) {
                errores.add("Archivo CSV vacío");
                return errores;
            }

            if (headerLine.startsWith("\uFEFF")) headerLine = headerLine.substring(1);
            headerLine = headerLine.replaceAll("[;,]\\s*$", "");
            char delimiter = headerLine.contains(";") ? ';' : ',';

            CSVParser parser = CSVFormat.DEFAULT.builder()
                    .setHeader(headerLine.split(String.valueOf(delimiter)))
                    .setSkipHeaderRecord(true)
                    .setIgnoreEmptyLines(true)
                    .setTrim(true)
                    .setDelimiter(delimiter)
                    .build()
                    .parse(reader);

            int fila = 1;
            for (CSVRecord record : parser) {
                fila++;
                List<String> filaErrores = validarRegistro(record.toMap(), fila);
                errores.addAll(filaErrores);

                if (!soloValidar && filaErrores.isEmpty()) {
                    clients.add(crearCliente(record.toMap()));
                }
            }

            if (!soloValidar && !clients.isEmpty()) {
                clientRepository.saveAll(clients);
            }

        } catch (Exception e) {
            errores.add("Error leyendo CSV: " + e.getMessage());
        }

        return errores;
    }

    // ========================
    // Import Excel
    // ========================
    private List<String> importExcel(MultipartFile file, boolean soloValidar) {
        List<String> errores = new ArrayList<>();
        List<Client> clients = new ArrayList<>();

        try (XSSFWorkbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            if (sheet == null) {
                errores.add("Hoja de Excel vacía");
                return errores;
            }

            int filaNum = 1;
            Row headerRow = sheet.getRow(0);
            if (headerRow == null) {
                errores.add("Encabezado de Excel vacío");
                return errores;
            }

            List<String> headers = new ArrayList<>();
            headerRow.forEach(cell -> headers.add(cell.getStringCellValue().trim().toLowerCase()));

            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue; // saltar encabezado
                filaNum++;

                Map<String, String> rowMap = new java.util.HashMap<>();
                for (int i = 0; i < headers.size(); i++) {
                    String value = "";
                    if (i < row.getLastCellNum() && row.getCell(i) != null) {
                        row.getCell(i).setCellType(CellType.STRING);
                        value = row.getCell(i).getStringCellValue().trim();
                    }
                    rowMap.put(headers.get(i), value);
                }

                List<String> filaErrores = validarRegistro(rowMap, filaNum);
                errores.addAll(filaErrores);

                if (!soloValidar && filaErrores.isEmpty()) {
                    clients.add(crearCliente(rowMap));
                }
            }

            if (!soloValidar && !clients.isEmpty()) {
                clientRepository.saveAll(clients);
            }

        } catch (Exception e) {
            errores.add("Error leyendo Excel: " + e.getMessage());
        }

        return errores;
    }

    // ========================
    // Validación por fila (Ignora filas vacías)
    // ========================
    private List<String> validarRegistro(Map<String, String> record, int fila) {
        List<String> errores = new ArrayList<>();

        String nombre = record.getOrDefault("nombre", "").trim();
        String email = record.getOrDefault("email", "").trim();
        String telefono = record.getOrDefault("telefono", "").trim();

        // Ignorar fila completamente vacía
        if (nombre.isEmpty() && email.isEmpty() && telefono.isEmpty()) {
            return errores; // no agrega errores
        }

        if (nombre.isEmpty()) errores.add("Fila " + fila + ": nombre vacío");
        if (email.isEmpty()) errores.add("Fila " + fila + ": email vacío");
        else if (!email.matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,6}$"))
            errores.add("Fila " + fila + ": email inválido");
        if (telefono.isEmpty()) errores.add("Fila " + fila + ": teléfono vacío");
        else if (!telefono.matches("\\d+")) errores.add("Fila " + fila + ": teléfono inválido");

        return errores;
    }

    // ========================
    // Crear objeto Client
    // ========================
    private Client crearCliente(Map<String, String> record) {
        Client client = new Client();
        client.setName(record.get("nombre"));
        client.setEmail(record.get("email"));
        client.setPhone(record.get("telefono"));
        return client;
    }
}
