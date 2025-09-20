package com.example.planifest.service;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.planifest.entity.Event;
import com.example.planifest.entity.Task;
import com.example.planifest.entity.User;
import com.example.planifest.enums.TaskStatus;
import com.example.planifest.repository.TaskRepository;

@Service
public class TaskExcelService {

    private final TaskRepository taskRepository;

    public TaskExcelService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    // Método principal para importar tareas y devolver duplicadas
    public List<String> importarTareasDesdeExcel(MultipartFile file) {
        List<Task> nuevasTareas = new ArrayList<>();
        List<String> duplicadas = new ArrayList<>();

        try (InputStream is = file.getInputStream();
             Workbook workbook = WorkbookFactory.create(is)) {

            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rows = sheet.iterator();

            int rowNumber = 0;
            while (rows.hasNext()) {
                Row row = rows.next();

                if (rowNumber == 0) { // saltar encabezado
                    rowNumber++;
                    continue;
                }

                Task task = new Task();

                // Fecha
                LocalDateTime fecha = parseDateCell(row.getCell(1));
                task.setDate(fecha);

                // Descripción
                task.setDescription(getCellStringValue(row.getCell(2)));

                // Nombre
                String nombre = getCellStringValue(row.getCell(3));
                task.setName(nombre);

                // Estado
                String statusStr = getCellStringValue(row.getCell(4)).toUpperCase();
                try {
                    task.setStatus(TaskStatus.valueOf(statusStr));
                } catch (IllegalArgumentException ex) {
                    throw new RuntimeException("Estado inválido en fila " + rowNumber + ": " + statusStr);
                }

                // Usuario
                Long userId = getCellLongValue(row.getCell(6));
                User user = new User();
                user.setId(userId);
                task.setUser(user);

                // Evento
                Long eventId = getCellLongValue(row.getCell(5));
                Event event = new Event();
                event.setId(eventId);
                task.setEvent(event);

                boolean exists = taskRepository.existsByNameAndDateAndUser_IdAndEvent_Id(
                        nombre,
                        fecha,
                        userId,
                        eventId
                );

                if (exists) {
                    duplicadas.add(nombre + " (usuario " + userId + ", evento " + eventId + ")");
                } else {
                    nuevasTareas.add(task);
                    taskRepository.save(task); // guardar la tarea
                }

                rowNumber++;
            }

        } catch (Exception e) {
            throw new RuntimeException("Error leyendo el archivo Excel: " + e.getMessage(), e);
        }

        return duplicadas;
    }

    // ===== HELPERS =====
    private String getCellStringValue(Cell cell) {
        if (cell == null) return "";
        cell.setCellType(CellType.STRING);
        return cell.getStringCellValue().trim();
    }

    private Long getCellLongValue(Cell cell) {
        if (cell == null) return null;
        return (long) cell.getNumericCellValue();
    }

    private LocalDateTime parseDateCell(Cell cell) {
        if (cell == null) return null;

        if (cell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(cell)) {
            return cell.getLocalDateTimeCellValue();
        } else {
            String fechaStr = cell.getStringCellValue().trim();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy H:mm");
            return LocalDateTime.parse(fechaStr, formatter);
        }
    }
}
