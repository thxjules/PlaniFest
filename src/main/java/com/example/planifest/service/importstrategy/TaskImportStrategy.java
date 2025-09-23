package com.example.planifest.service.importstrategy;

import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.planifest.entity.Event;
import com.example.planifest.entity.Task;
import com.example.planifest.entity.User;
import com.example.planifest.enums.TaskStatus;
import com.example.planifest.repository.TaskRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class TaskImportStrategy implements ImportStrategy<Task> {

    private final TaskRepository taskRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    // ✅ Soporta múltiples formatos de fecha (con o sin hora)
    private static final List<DateTimeFormatter> FORMATOS_FECHA = Arrays.asList(
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm"),
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"),
            DateTimeFormatter.ofPattern("dd/MM/yyyy H:mm"),
            DateTimeFormatter.ofPattern("yyyy-MM-dd"),
            DateTimeFormatter.ofPattern("dd/MM/yyyy")
    );

    public TaskImportStrategy(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @Override
    public List<String> validate(MultipartFile file) {
        List<String> errores = new ArrayList<>();
        try {
            String name = file.getOriginalFilename();
            if (name == null) {
                errores.add("Archivo inválido");
                return errores;
            }

            if (name.endsWith(".json")) {
                List<Task> tasks = Arrays.asList(objectMapper.readValue(file.getInputStream(), Task[].class));
                for (Task t : tasks) {
                    validarTask(t, errores);
                }
            } else if (name.endsWith(".csv")) {
                Reader reader = new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8);
                CSVParser parser = new CSVParser(reader, CSVFormat.DEFAULT
                        .withDelimiter(';')
                        .withFirstRecordAsHeader()
                        .withIgnoreHeaderCase()
                        .withTrim());
                for (CSVRecord record : parser) {
                    Task t = csvToTask(record);
                    validarTask(t, errores);
                }
                parser.close();
            } else if (name.endsWith(".xlsx")) {
                Workbook workbook = new XSSFWorkbook(file.getInputStream());
                Sheet sheet = workbook.getSheetAt(0);
                int rowNum = 0;
                for (Row row : sheet) {
                    if (rowNum++ == 0) continue; // saltar encabezado
                    Task t = excelToTask(row);
                    validarTask(t, errores);
                }
                workbook.close();
            } else {
                errores.add("Formato no soportado: " + name);
            }
        } catch (Exception e) {
            errores.add("Error leyendo el archivo: " + e.getMessage());
        }
        return errores;
    }

    @Override
    public void saveAll(MultipartFile file) {
        try {
            String name = file.getOriginalFilename();
            if (name == null) throw new RuntimeException("Archivo inválido");

            List<Task> tasks = new ArrayList<>();

            if (name.endsWith(".json")) {
                tasks = Arrays.asList(objectMapper.readValue(file.getInputStream(), Task[].class));
            } else if (name.endsWith(".csv")) {
                Reader reader = new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8);
                CSVParser parser = new CSVParser(reader, CSVFormat.DEFAULT
                        .withDelimiter(';')
                        .withFirstRecordAsHeader()
                        .withIgnoreHeaderCase()
                        .withTrim());
                for (CSVRecord record : parser) {
                    tasks.add(csvToTask(record));
                }
                parser.close();
            } else if (name.endsWith(".xlsx")) {
                Workbook workbook = new XSSFWorkbook(file.getInputStream());
                Sheet sheet = workbook.getSheetAt(0);
                int rowNum = 0;
                for (Row row : sheet) {
                    if (rowNum++ == 0) continue; // encabezado
                    tasks.add(excelToTask(row));
                }
                workbook.close();
            } else {
                throw new RuntimeException("Formato no soportado: " + name);
            }

            // ✅ Guardar en BD evitando duplicados
            for (Task t : tasks) {
                boolean exists = taskRepository.existsByNameAndDateAndUser_IdAndEvent_Id(
                        t.getName(), t.getDate(), t.getUser().getId(), t.getEvent().getId()
                );
                if (!exists) taskRepository.save(t);
            }

        } catch (Exception e) {
            throw new RuntimeException("Error guardando tareas: " + e.getMessage(), e);
        }
    }

    // ================== Helpers ==================

    private void validarTask(Task t, List<String> errores) {
        if (t.getName() == null || t.getName().isEmpty())
            errores.add("Nombre vacío");
        if (t.getDate() == null)
            errores.add("Fecha inválida para tarea: " + t.getName());
        if (t.getUser() == null || t.getUser().getId() == null)
            errores.add("Usuario no definido para tarea: " + t.getName());
        if (t.getEvent() == null || t.getEvent().getId() == null)
            errores.add("Evento no definido para tarea: " + t.getName());
        if (t.getStatus() == null)
            errores.add("Estado no definido para tarea: " + t.getName());
    }

    private LocalDateTime parseFecha(String fechaStr) {
        String f = fechaStr.trim(); // elimina espacios al inicio y fin
        for (DateTimeFormatter formatter : FORMATOS_FECHA) {
            try {
                if (formatter.toString().contains("H") || formatter.toString().contains("m")) {
                    // formato con hora
                    return LocalDateTime.parse(f, formatter);
                } else {
                    // formato solo fecha
                    LocalDate date = LocalDate.parse(f, formatter);
                    return LocalDateTime.of(date, LocalTime.MIDNIGHT);
                }
            } catch (Exception ignored) {}
        }
        throw new RuntimeException("Formato de fecha no soportado: " + fechaStr);
    }

    private Task csvToTask(CSVRecord record) {
        Task t = new Task();
        t.setName(record.get("name").trim());
        t.setDescription(record.get("description").trim());
        t.setStatus(TaskStatus.valueOf(record.get("status").toUpperCase().trim()));
        t.setDate(parseFecha(record.get("date")));
        User u = new User();
        u.setId(Long.parseLong(record.get("user_id").trim()));
        t.setUser(u);
        Event e = new Event();
        e.setId(Long.parseLong(record.get("event_id").trim()));
        t.setEvent(e);
        return t;
    }

    private Task excelToTask(Row row) {
        Task t = new Task();
        t.setDate(row.getCell(0).getLocalDateTimeCellValue());
        t.setDescription(row.getCell(1).getStringCellValue().trim());
        t.setName(row.getCell(2).getStringCellValue().trim());
        t.setStatus(TaskStatus.valueOf(row.getCell(3).getStringCellValue().toUpperCase().trim()));
        User u = new User();
        u.setId((long) row.getCell(4).getNumericCellValue());
        t.setUser(u);
        Event e = new Event();
        e.setId((long) row.getCell(5).getNumericCellValue());
        t.setEvent(e);
        return t;
    }
}
