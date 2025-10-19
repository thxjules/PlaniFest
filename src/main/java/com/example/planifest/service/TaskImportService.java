package com.example.planifest.service;

import com.example.planifest.entity.Event;
import com.example.planifest.entity.Task;
import com.example.planifest.entity.User;
import com.example.planifest.enums.TaskStatus;
import com.example.planifest.repository.TaskRepository;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class TaskImportService {

    private final TaskRepository taskRepository;
    private final UserServiceImp userService;
    private final EventServiceImp eventService;

    public TaskImportService(TaskRepository taskRepository,
            UserServiceImp userService,
            EventServiceImp eventService) {
        this.taskRepository = taskRepository;
        this.userService = userService;
        this.eventService = eventService;
    }

    // ================================ IMPORTAR ARCHIVO
    // =================================
    public Map<String, List<String>> importarArchivo(MultipartFile file) throws Exception {
        String filename = file.getOriginalFilename();
        if (filename == null)
            throw new Exception("Archivo sin nombre");

        if (filename.endsWith(".csv")) {
            return importarCSV(file);
        } else if (filename.endsWith(".xls") || filename.endsWith(".xlsx")) {
            return importarExcel(file);
        } else {
            throw new Exception("Formato de archivo no soportado: " + filename);
        }
    }

    // ================================ CSV =================================
    private Map<String, List<String>> importarCSV(MultipartFile file) throws Exception {
        List<String> duplicadas = new ArrayList<>();
        List<String> errores = new ArrayList<>();

        BufferedReader reader = new BufferedReader(
                new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8));

        // Eliminar BOM
        reader.mark(1);
        if (reader.read() != 0xFEFF)
            reader.reset();

        // Leer encabezado
        String headerLine = reader.readLine();
        if (headerLine == null) {
            errores.add("Archivo CSV vacío");
            return Map.of("duplicadas", duplicadas, "errores", errores);
        }

        // Detectar delimitador
        char delimiter = detectarDelimitador(headerLine);

        // Normalizar headers
        String[] headers = Arrays.stream(headerLine.split(String.valueOf(delimiter)))
                .map(h -> h.replace("\uFEFF", "").trim().toLowerCase())
                .toArray(String[]::new);

        CSVParser parser = CSVParser.parse(reader, CSVFormat.DEFAULT
                .withDelimiter(delimiter)
                .withHeader(headers)
                .withSkipHeaderRecord()
                .withIgnoreSurroundingSpaces()
                .withTrim());

        for (CSVRecord record : parser) {
            try {
                procesarFila(record.get("name"), record.get("description"),
                        record.get("date"), record.get("status"),
                        record.get("user_id"), record.get("event_id"),
                        duplicadas);
            } catch (Exception e) {
                errores.add("Fila con error: " + e.getMessage());
            }
        }

        return Map.of("duplicadas", duplicadas, "errores", errores);
    }

    private char detectarDelimitador(String headerLine) {
        Map<Character, Integer> counts = new HashMap<>();
        counts.put(',', headerLine.length() - headerLine.replace(",", "").length());
        counts.put(';', headerLine.length() - headerLine.replace(";", "").length());
        counts.put('\t', headerLine.length() - headerLine.replace("\t", "").length());
        return counts.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(',');
    }

    // ================================ EXCEL =================================
    private Map<String, List<String>> importarExcel(MultipartFile file) throws Exception {
        List<String> duplicadas = new ArrayList<>();
        List<String> errores = new ArrayList<>();

        Workbook workbook = new XSSFWorkbook(file.getInputStream());
        Sheet sheet = workbook.getSheetAt(0);

        Iterator<Row> rowIterator = sheet.iterator();
        if (!rowIterator.hasNext()) {
            errores.add("Archivo Excel vacío");
            workbook.close();
            return Map.of("duplicadas", duplicadas, "errores", errores);
        }

        Row headerRow = rowIterator.next();
        Map<String, Integer> headerMap = new HashMap<>();
        for (Cell cell : headerRow) {
            String header = cell.getStringCellValue().trim().toLowerCase().replace("\uFEFF", "");
            headerMap.put(header, cell.getColumnIndex());
        }

        while (rowIterator.hasNext()) {
            Row row = rowIterator.next();
            try {
                procesarFila(
                        getCellString(row.getCell(headerMap.get("name"))),
                        getCellString(row.getCell(headerMap.get("description"))),
                        getCellString(row.getCell(headerMap.get("date"))),
                        getCellString(row.getCell(headerMap.get("status"))),
                        getCellString(row.getCell(headerMap.get("user_id"))),
                        getCellString(row.getCell(headerMap.get("event_id"))),
                        duplicadas);
            } catch (Exception e) {
                errores.add("Fila con error: " + e.getMessage());
            }
        }

        workbook.close();
        return Map.of("duplicadas", duplicadas, "errores", errores);
    }

    private String getCellString(Cell cell) {
        if (cell == null)
            return "";
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue().trim();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    LocalDateTime dateTime = cell.getLocalDateTimeCellValue();
                    return dateTime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy H:mm"));
                } else
                    return String.valueOf((long) cell.getNumericCellValue());
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            case BLANK:
            default:
                return "";
        }
    }

    // ================================ AUXILIARES =================================
    private void procesarFila(String nombreTarea, String desc, String fechaStr, String estado,
            String userIdStr, String eventIdStr, List<String> duplicadas) throws Exception {
        Long userId = Long.parseLong(userIdStr.trim());
        Long eventId = Long.parseLong(eventIdStr.trim());
        LocalDateTime fecha = parseFecha(fechaStr);
        TaskStatus status = parseStatus(estado);

        User user = userService.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + userId));
        Event event = eventService.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Evento no encontrado con ID: " + eventId));

        Task task = new Task();
        task.setName(nombreTarea);
        task.setDescription(desc);
        task.setStatus(status);
        task.setDate(fecha);
        task.setUser(user);
        task.setEvent(event);

        boolean exists = taskRepository.existsByNameAndDateAndUser_IdAndEvent_Id(
                nombreTarea, fecha, userId, eventId);
        if (exists)
            duplicadas.add(nombreTarea);
        else
            taskRepository.save(task);
    }

    private LocalDateTime parseFecha(String fechaStr) {
        List<String> patterns = Arrays.asList(
                "dd/MM/yyyy H:mm", "dd/MM/yyyy",
                "dd-MM-yyyy H:mm", "dd-MM-yyyy",
                "yyyy/MM/dd H:mm", "yyyy/MM/dd",
                "yyyy-MM-dd H:mm", "yyyy-MM-dd");
        for (String pattern : patterns) {
            try {
                return LocalDateTime.parse(fechaStr.trim(), DateTimeFormatter.ofPattern(pattern));
            } catch (Exception ignored) {
            }
        }
        return null;
    }

    private TaskStatus parseStatus(String estado) {
        switch (estado.toUpperCase()) {
            case "PENDING":
                return TaskStatus.PENDING;
            case "IN_PROGRESS":
                return TaskStatus.IN_PROGRESS;
            case "COMPLETE":
                return TaskStatus.COMPLETED;
            default:
                return TaskStatus.PENDING;
        }
    }

}
