package com.example.planifest.service.importstrategy;

import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.poi.ss.usermodel.Cell;
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
import com.example.planifest.repository.EventRepository;
import com.example.planifest.repository.TaskRepository;
import com.example.planifest.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class TaskImportStrategy implements ImportStrategy<Task> {

    private final TaskRepository taskRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final List<DateTimeFormatter> FORMATOS_FECHA = Arrays.asList(
            DateTimeFormatter.ofPattern("d/M/yyyy HH:mm"),
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"),
            DateTimeFormatter.ofPattern("d/M/yyyy"),
            DateTimeFormatter.ofPattern("dd/MM/yyyy"),
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"),
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
    );

    public TaskImportStrategy(TaskRepository taskRepository,
                              EventRepository eventRepository,
                              UserRepository userRepository) {
        this.taskRepository = taskRepository;
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
    }

    // ================== VALIDATE ==================
    @Override
    public List<String> validate(MultipartFile file) {
        List<String> errores = new ArrayList<>();
        try {
            List<Task> tasks = cargarTasks(file);

            for (Task t : tasks) {
                validarTask(t, errores);

                if (t.getUser() != null && !userRepository.existsById(t.getUser().getId())) {
                    errores.add("Usuario inexistente: " + t.getName() + " (user_id=" + t.getUser().getId() + ")");
                }
                if (t.getEvent() != null && !eventRepository.existsById(t.getEvent().getId())) {
                    errores.add("Evento inexistente: " + t.getName() + " (event_id=" + t.getEvent().getId() + ")");
                }
            }

        } catch (Exception e) {
            errores.add("Error leyendo el archivo: " + e.getMessage());
        }
        return errores;
    }

    // ================== SAVE ALL ==================
    @Override
    public void saveAll(MultipartFile file) {
        try {
            List<Task> tasks = cargarTasks(file);

            for (Task t : tasks) {
                // Validar existencia de usuario y evento
                if (t.getUser() == null || !userRepository.existsById(t.getUser().getId())) {
                    System.out.println("Omitida tarea por usuario inexistente: " + t.getName());
                    continue;
                }
                if (t.getEvent() == null || !eventRepository.existsById(t.getEvent().getId())) {
                    System.out.println("Omitida tarea por evento inexistente: " + t.getName());
                    continue;
                }

                // Evitar duplicados por name + user + event + date
                boolean exists = taskRepository.existsByNameAndDateBetweenAndUser_IdAndEvent_Id(
                        t.getName().trim().toLowerCase(),
                        t.getDate().toLocalDate().atStartOfDay(),
                        t.getDate().toLocalDate().atTime(23, 59, 59),
                        t.getUser().getId(),
                        t.getEvent().getId()
                );

                if (!exists) {
                    t.setName(t.getName().trim().toLowerCase());
                    taskRepository.save(t); // ID generado automáticamente
                } else {
                    System.out.println("Omitida tarea duplicada: " + t.getName());
                }
            }

        } catch (Exception e) {
            throw new RuntimeException("Error guardando tareas: " + e.getMessage(), e);
        }
    }

    // ================== HELPERS ==================
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

    private List<Task> cargarTasks(MultipartFile file) throws Exception {
        List<Task> tasks = new ArrayList<>();
        String name = file.getOriginalFilename();
        if (name == null) throw new RuntimeException("Archivo inválido");

        if (name.endsWith(".json")) {
            tasks = Arrays.asList(objectMapper.readValue(file.getInputStream(), Task[].class));
        } else if (name.endsWith(".csv") || name.endsWith(".tsv")) {
            char delimiter = name.endsWith(".tsv") ? '\t' : ';';
            Reader reader = new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8);
            CSVParser parser = new CSVParser(reader, CSVFormat.DEFAULT
                    .withDelimiter(delimiter)
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
                if (rowNum++ == 0) continue; // saltar encabezado
                tasks.add(excelToTask(row));
            }
            workbook.close();
        } else {
            throw new RuntimeException("Formato no soportado: " + name);
        }

        return tasks;
    }

    private Task csvToTask(CSVRecord record) {
        Task t = new Task();
        t.setName(record.get("name").trim());
        t.setDescription(record.get("description").trim());
        t.setStatus(TaskStatus.valueOf(record.get("status").toUpperCase().trim()));
        t.setDate(parseFechaFlexible(record.get("date")));

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

        t.setDate(getCellDateValue(row.getCell(0)));
        t.setDescription(getCellStringValue(row.getCell(1)));
        t.setName(getCellStringValue(row.getCell(2)));
        t.setStatus(TaskStatus.valueOf(getCellStringValue(row.getCell(3)).toUpperCase()));

        User u = new User();
        u.setId((long) getCellNumericValue(row.getCell(4)));
        t.setUser(u);

        Event e = new Event();
        e.setId((long) getCellNumericValue(row.getCell(5)));
        t.setEvent(e);

        return t;
    }

    // ================== Cell Helpers ==================
    private String getCellStringValue(Cell cell) {
        if (cell == null) return "";
        switch (cell.getCellType()) {
            case STRING: return cell.getStringCellValue().trim();
            case NUMERIC: return String.valueOf((long) cell.getNumericCellValue());
            case BOOLEAN: return String.valueOf(cell.getBooleanCellValue());
            case FORMULA: return cell.getCellFormula();
            default: return "";
        }
    }

    private double getCellNumericValue(Cell cell) {
        if (cell == null) return 0;
        switch (cell.getCellType()) {
            case NUMERIC: return cell.getNumericCellValue();
            case STRING: return Double.parseDouble(cell.getStringCellValue().trim());
            default: return 0;
        }
    }

    private LocalDateTime getCellDateValue(Cell cell) {
        if (cell == null) return null;
        try {
            return cell.getLocalDateTimeCellValue();
        } catch (Exception e) {
            return parseFechaFlexible(getCellStringValue(cell));
        }
    }

    private LocalDateTime parseFechaFlexible(String fechaStr) {
        if (fechaStr == null || fechaStr.trim().isEmpty())
            throw new RuntimeException("La fecha está vacía");

        String f = fechaStr.trim().replace(" 0:00", " 00:00");

        for (DateTimeFormatter formatter : FORMATOS_FECHA) {
            try {
                return LocalDateTime.parse(f, formatter);
            } catch (Exception e1) {
                try {
                    LocalDate d = LocalDate.parse(f, formatter);
                    return d.atStartOfDay();
                } catch (Exception ignored) {}
            }
        }
        throw new RuntimeException("Formato de fecha no soportado: " + fechaStr);
    }
}
