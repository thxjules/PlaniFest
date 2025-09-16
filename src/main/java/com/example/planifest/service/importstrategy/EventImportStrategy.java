package com.example.planifest.service.importstrategy;

import java.io.InputStreamReader;
import java.io.Reader;
import java.time.LocalDate;
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
import com.example.planifest.repository.EventRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class EventImportStrategy implements ImportStrategy<Event> {

    private final EventRepository eventRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public EventImportStrategy(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    @Override
    public List<String> validate(MultipartFile file) {
        List<String> errores = new ArrayList<>();
        try {
            String filename = file.getOriginalFilename();
            if (filename == null) {
                errores.add("Archivo inválido");
                return errores;
            }

            List<Event> events = parseFile(file);

            // Validaciones de campos obligatorios
            for (Event e : events) {
                if (e.getEventName() == null || e.getEventName().isBlank()) {
                    errores.add("Nombre de evento vacío");
                }
                if (e.getDate() == null) {
                    errores.add("Fecha inválida para evento: " + e.getEventName());
                }
                if (e.getLocation() == null || e.getLocation().isBlank()) {
                    errores.add("Ubicación vacía para evento: " + e.getEventName());
                }
            }

        } catch (Exception e) {
            errores.add("Error leyendo el archivo: " + e.getMessage());
        }
        return errores;
    }

    @Override
    public void saveAll(MultipartFile file) {
        try {
            List<Event> events = parseFile(file);

            for (Event e : events) {
                // Evitar duplicados por nombre y fecha
                boolean exists = eventRepository.findByEventName(e.getEventName())
                                .filter(ev -> ev.getDate().equals(e.getDate()))
                                .isPresent();
                if (!exists) {
                    eventRepository.save(e);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Error guardando eventos: " + e.getMessage(), e);
        }
    }

    // ================== Helpers ==================
    private List<Event> parseFile(MultipartFile file) throws Exception {
        List<Event> events = new ArrayList<>();
        String filename = file.getOriginalFilename();
        if (filename == null) throw new RuntimeException("Archivo inválido");

        if (filename.endsWith(".json")) {
            events = Arrays.asList(objectMapper.readValue(file.getInputStream(), Event[].class));
        } else if (filename.endsWith(".csv")) {
            Reader reader = new InputStreamReader(file.getInputStream());
            CSVParser parser = new CSVParser(reader, CSVFormat.DEFAULT.withFirstRecordAsHeader());
            for (CSVRecord record : parser) {
                events.add(csvToEvent(record));
            }
        } else if (filename.endsWith(".xlsx")) {
            Workbook workbook = new XSSFWorkbook(file.getInputStream());
            Sheet sheet = workbook.getSheetAt(0);
            int rowNum = 0;
            for (Row row : sheet) {
                if (rowNum++ == 0) continue; // saltar encabezado
                events.add(excelToEvent(row));
            }
            workbook.close();
        } else {
            throw new RuntimeException("Formato no soportado: " + filename);
        }

        return events;
    }

    private Event csvToEvent(CSVRecord record) {
        Event e = new Event();
        e.setEventName(record.get("eventName"));
        e.setDescription(record.get("description"));
        e.setLocation(record.get("location"));
        e.setDate(LocalDate.parse(record.get("date"), DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        return e;
    }

    private Event excelToEvent(Row row) {
        Event e = new Event();
        e.setDate(row.getCell(0).getLocalDateTimeCellValue().toLocalDate());
        e.setDescription(row.getCell(1).getStringCellValue());
        e.setEventName(row.getCell(2).getStringCellValue());
        e.setLocation(row.getCell(3).getStringCellValue());
        return e;
    }
}
