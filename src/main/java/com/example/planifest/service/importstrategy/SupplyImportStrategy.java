package com.example.planifest.service.importstrategy;

import com.example.planifest.entity.Supply;
import com.example.planifest.enums.SupplyStatus;
import com.example.planifest.repository.SupplyRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Service
public class SupplyImportStrategy implements ImportStrategy<Supply> {

    private final SupplyRepository supplyRepository;

    public SupplyImportStrategy(SupplyRepository supplyRepository) {
        this.supplyRepository = supplyRepository;
    }

    @Override
    public List<String> validate(MultipartFile file) {
        List<String> errores = new ArrayList<>();

        try (InputStream is = file.getInputStream(); Workbook workbook = new XSSFWorkbook(is)) {
            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rows = sheet.iterator();

            int rowNumber = 0;
            while (rows.hasNext()) {
                Row row = rows.next();

                if (rowNumber == 0) { // Saltar encabezado
                    rowNumber++;
                    continue;
                }

                String name = getCellStringValue(row.getCell(0));
                String supplyType = getCellStringValue(row.getCell(1));
                String description = getCellStringValue(row.getCell(2));
                String storageLocation = getCellStringValue(row.getCell(3));
                String statusStr = getCellStringValue(row.getCell(4));
                Integer currentStock = getCellIntValue(row.getCell(5));
                Integer minStock = getCellIntValue(row.getCell(6));
                Integer maxStock = getCellIntValue(row.getCell(7));
                String packagingUnit = getCellStringValue(row.getCell(8));

                // Validaciones básicas
                if (name.isEmpty()) errores.add("Fila " + rowNumber + ": Nombre vacío");
                if (supplyType.isEmpty()) errores.add("Fila " + rowNumber + ": Tipo vacío");
                if (description.isEmpty()) errores.add("Fila " + rowNumber + ": Descripción vacía");
                if (storageLocation.isEmpty()) errores.add("Fila " + rowNumber + ": Ubicación vacía");
                if (statusStr.isEmpty()) errores.add("Fila " + rowNumber + ": Estado vacío");
                if (currentStock == null) errores.add("Fila " + rowNumber + ": Stock actual inválido");
                if (minStock == null) errores.add("Fila " + rowNumber + ": Stock mínimo inválido");
                if (maxStock == null) errores.add("Fila " + rowNumber + ": Stock máximo inválido");
                if (packagingUnit.isEmpty()) errores.add("Fila " + rowNumber + ": Unidad de empaque vacía");

                // Validar estado
                try {
                    SupplyStatus.valueOf(statusStr.toUpperCase());
                } catch (IllegalArgumentException e) {
                    errores.add("Fila " + rowNumber + ": Estado inválido -> " + statusStr);
                }

                // Validar duplicado por nombre
                if (supplyRepository.existsByName(name)) {
                    errores.add("Fila " + rowNumber + ": Supply duplicado -> " + name);
                }

                rowNumber++;
            }

        } catch (Exception e) {
            errores.add("Error leyendo el archivo: " + e.getMessage());
        }

        return errores;
    }

    @Override
    public void saveAll(MultipartFile file) {
        try (InputStream is = file.getInputStream(); Workbook workbook = new XSSFWorkbook(is)) {
            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rows = sheet.iterator();

            int rowNumber = 0;
            while (rows.hasNext()) {
                Row row = rows.next();
                if (rowNumber == 0) { // Saltar encabezado
                    rowNumber++;
                    continue;
                }

                Supply supply = new Supply();
                supply.setName(getCellStringValue(row.getCell(0)));
                supply.setSupplyType(getCellStringValue(row.getCell(1)));
                supply.setDescription(getCellStringValue(row.getCell(2)));
                supply.setStorageLocation(getCellStringValue(row.getCell(3)));

                String statusStr = getCellStringValue(row.getCell(4));
                supply.setStatus(SupplyStatus.valueOf(statusStr.toUpperCase()));

                supply.setCurrentStock(getCellIntValue(row.getCell(5)));
                supply.setMinStock(getCellIntValue(row.getCell(6)));
                supply.setMaxStock(getCellIntValue(row.getCell(7)));
                supply.setPackagingUnit(getCellStringValue(row.getCell(8)));

                // Guardar si no existe
                if (!supplyRepository.existsByName(supply.getName())) {
                    supplyRepository.save(supply);
                }

                rowNumber++;
            }
        } catch (Exception e) {
            throw new RuntimeException("Error importando supplies: " + e.getMessage(), e);
        }
    }

    // ===== HELPERS =====
    private String getCellStringValue(Cell cell) {
        if (cell == null) return "";
        cell.setCellType(CellType.STRING);
        return cell.getStringCellValue().trim();
    }

    private Integer getCellIntValue(Cell cell) {
        if (cell == null) return null;
        if (cell.getCellType() == CellType.NUMERIC) {
            return (int) cell.getNumericCellValue();
        } else {
            try {
                return Integer.parseInt(cell.getStringCellValue());
            } catch (NumberFormatException e) {
                return null;
            }
        }
    }
}
