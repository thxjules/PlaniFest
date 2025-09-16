package com.example.planifest.service.importstrategy;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.planifest.entity.User;
import com.example.planifest.repository.UserRepository;

@Service
public class UserImportStrategy implements ImportStrategy<User> {

    private final UserRepository userRepository;

    public UserImportStrategy(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public List<String> validate(MultipartFile file) {
        List<String> errores = new ArrayList<>();
        try (InputStream is = file.getInputStream();
             Workbook workbook = new XSSFWorkbook(is)) {

            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rows = sheet.iterator();
            int rowNumber = 0;

            while (rows.hasNext()) {
                Row row = rows.next();
                if (rowNumber++ == 0) continue; // saltar encabezado

                String username = getCellStringValue(row.getCell(0));
                String email = getCellStringValue(row.getCell(1));

                // Validaciones
                if (username.isEmpty()) errores.add("Fila " + rowNumber + ": username vacío");
                if (email.isEmpty()) errores.add("Fila " + rowNumber + ": email vacío");

                boolean exists = userRepository.existsByUsername(username);
                if (exists) errores.add("Fila " + rowNumber + ": username duplicado (" + username + ")");
            }

        } catch (Exception e) {
            errores.add("Error leyendo archivo: " + e.getMessage());
        }
        return errores;
    }

    @Override
    public void saveAll(MultipartFile file) {
        try (InputStream is = file.getInputStream();
             Workbook workbook = new XSSFWorkbook(is)) {

            Sheet sheet = workbook.getSheetAt(0);
            List<User> users = new ArrayList<>();
            int rowNumber = 0;

            for (Row row : sheet) {
                if (rowNumber++ == 0) continue; // encabezado
                String username = getCellStringValue(row.getCell(0));
                String email = getCellStringValue(row.getCell(1));

                User u = new User();
                u.setUsername(username);
                u.setEmail(email);
                users.add(u);
            }

            userRepository.saveAll(users);

        } catch (Exception e) {
            throw new RuntimeException("Error guardando usuarios: " + e.getMessage(), e);
        }
    }

    // ===== Helpers =====
    private String getCellStringValue(Cell cell) {
        if (cell == null) return "";
        cell.setCellType(CellType.STRING);
        return cell.getStringCellValue().trim();
    }
}
