package com.example.planifest.service.importstrategy;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;
import java.util.*;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.planifest.entity.Event;
import com.example.planifest.entity.Task;
import com.example.planifest.entity.User;
import com.example.planifest.enums.TaskStatus;
import com.example.planifest.repository.EventRepository;
import com.example.planifest.repository.TaskRepository;
import com.example.planifest.repository.UserRepository;

@Service
public class TaskImportStrategy implements ImportStrategy<Task> {

    private final TaskRepository taskRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    public TaskImportStrategy(TaskRepository taskRepository,
                              EventRepository eventRepository,
                              UserRepository userRepository) {
        this.taskRepository = taskRepository;
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
    }

    @Override
    public List<String> validate(MultipartFile file) {
        return importarArchivo(file, true);
    }

    @Override
    public void saveAll(MultipartFile file) {
        importarArchivo(file, false);
    }

    private List<String> importarArchivo(MultipartFile file, boolean soloValidar) {
        List<String> errores = new ArrayList<>();
        try {
            String nombre = file.getOriginalFilename();
            if (nombre == null) {
                errores.add("Archivo sin nombre");
                return errores;
            }

            if (nombre.endsWith(".csv")) {
                errores.addAll(importarCSV(file, soloValidar));
            } else if (nombre.endsWith(".xlsx")) {
                errores.addAll(importarExcel(file, soloValidar));
            } else {
                errores.add("Formato no soportado. Solo CSV o XLSX.");
            }

        } catch (Exception e) {
            errores.add("Error procesando archivo: " + e.getMessage());
        }
        return errores;
    }

    // ================== CSV ==================
    private List<String> importarCSV(MultipartFile file, boolean soloValidar) {
        List<String> errores = new ArrayList<>();
        List<Task> tasks = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {

            String headerLine = reader.readLine();
            if (headerLine == null) {
                errores.add("Archivo CSV vacío");
                return errores;
            }

            if (headerLine.startsWith("\uFEFF")) headerLine = headerLine.substring(1);
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
                Map<String, String> datos = record.toMap();
                List<String> filaErrores = validarFila(datos, fila);
                errores.addAll(filaErrores);

                if (!soloValidar && filaErrores.isEmpty()) {
                    tasks.add(crearTask(datos));
                }
            }

            if (!soloValidar && !tasks.isEmpty()) {
                taskRepository.saveAll(tasks);
            }

        } catch (Exception e) {
            errores.add("Error leyendo CSV: " + e.getMessage());
        }

        return errores;
    }

    // ================== EXCEL ==================
    private List<String> importarExcel(MultipartFile file, boolean soloValidar) {
        List<String> errores = new ArrayList<>();
        List<Task> tasks = new ArrayList<>();

        try (XSSFWorkbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            if (sheet == null) {
                errores.add("Hoja de Excel vacía");
                return errores;
            }

            Row headerRow = sheet.getRow(0);
            if (headerRow == null) {
                errores.add("Encabezado de Excel vacío");
                return errores;
            }

            List<String> headers = new ArrayList<>();
            for (Cell cell : headerRow) {
                headers.add(cell.getStringCellValue().trim().toLowerCase());
            }

            int filaNum = 1;
            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue;
                filaNum++;

                Map<String, String> datos = new HashMap<>();
                for (int i = 0; i < headers.size(); i++) {
                    String valor = "";
                    Cell cell = row.getCell(i);
                    if (cell != null) {
                        switch (cell.getCellType()) {
                            case STRING -> valor = cell.getStringCellValue().trim();
                            case NUMERIC -> valor = String.valueOf(cell.getNumericCellValue()).trim();
                            case BOOLEAN -> valor = String.valueOf(cell.getBooleanCellValue()).trim();
                            case FORMULA -> valor = cell.getCellFormula().trim();
                            default -> valor = "";
                        }
                    }
                    datos.put(headers.get(i), valor);
                }

                List<String> filaErrores = validarFila(datos, filaNum);
                errores.addAll(filaErrores);

                if (!soloValidar && filaErrores.isEmpty()) {
                    tasks.add(crearTask(datos));
                }
            }

            if (!soloValidar && !tasks.isEmpty()) {
                taskRepository.saveAll(tasks);
            }

        } catch (Exception e) {
            errores.add("Error leyendo Excel: " + e.getMessage());
        }

        return errores;
    }

    // ================== VALIDACIÓN ==================
    private List<String> validarFila(Map<String, String> datos, int fila) {
        List<String> errores = new ArrayList<>();

        String name = datos.getOrDefault("name", "").trim();
        String date = datos.getOrDefault("date", "").trim();
        String status = datos.getOrDefault("status", "").trim();
        String userId = datos.getOrDefault("user_id", "").trim();
        String eventId = datos.getOrDefault("event_id", "").trim();

        if (name.isEmpty() && date.isEmpty() && status.isEmpty() && userId.isEmpty() && eventId.isEmpty()) {
            return errores;
        }

        if (name.isEmpty()) errores.add("Fila " + fila + ": nombre vacío");
        if (date.isEmpty()) errores.add("Fila " + fila + ": fecha vacía");
        if (status.isEmpty()) errores.add("Fila " + fila + ": estado vacío");
        if (userId.isEmpty()) errores.add("Fila " + fila + ": usuario vacío");
        if (eventId.isEmpty()) errores.add("Fila " + fila + ": evento vacío");

        try { Long.parseLong(userId); } catch (Exception e) { errores.add("Fila " + fila + ": ID de usuario inválido"); }
        try { Long.parseLong(eventId); } catch (Exception e) { errores.add("Fila " + fila + ": ID de evento inválido"); }
        try { TaskStatus.valueOf(status.toUpperCase()); } catch (Exception e) { errores.add("Fila " + fila + ": estado inválido"); }

        try { parseFechaFlexible(date); } catch (Exception e) { errores.add("Fila " + fila + ": fecha inválida"); }

        return errores;
    }

    // ================== CREACIÓN DE TASK ==================
    private Task crearTask(Map<String, String> datos) {
        Task t = new Task();
        t.setName(datos.get("name").trim());
        t.setDescription(datos.getOrDefault("description", "").trim());
        t.setStatus(TaskStatus.valueOf(datos.get("status").toUpperCase().trim()));
        t.setDate(parseFechaFlexible(datos.get("date").trim()));

        User u = new User();
        u.setId(Long.parseLong(datos.get("user_id").trim()));
        t.setUser(u);

        Event e = new Event();
        e.setId(Long.parseLong(datos.get("event_id").trim()));
        t.setEvent(e);

        return t;
    }

    // ================== PARSE FECHA FLEXIBLE ==================
    private LocalDateTime parseFechaFlexible(String fechaStr) {
        List<DateTimeFormatter> formatos = List.of(
                DateTimeFormatter.ofPattern("d/M/yyyy H:mm"),
                DateTimeFormatter.ofPattern("dd/MM/yyyy H:mm"),
                DateTimeFormatter.ofPattern("d/M/yyyy"),
                DateTimeFormatter.ofPattern("dd/MM/yyyy"),
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"),
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
        );

        String f = fechaStr.trim().replace(" 0:00", " 00:00");

        for (DateTimeFormatter formatter : formatos) {
            try {
                return LocalDateTime.parse(f, formatter.withResolverStyle(ResolverStyle.STRICT));
            } catch (Exception e) {
                try {
                    return LocalDateTime.of(LocalDateTime.parse(f, formatter).toLocalDate(), LocalDateTime.MIN.toLocalTime());
                } catch (Exception ignored) {}
            }
        }

        throw new RuntimeException("No se pudo parsear la fecha: " + fechaStr);
    }
}
