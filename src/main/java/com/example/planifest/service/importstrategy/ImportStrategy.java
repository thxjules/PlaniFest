package com.example.planifest.service.importstrategy;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

public interface ImportStrategy<T> {
     List<String> validate(MultipartFile file);
    void saveAll(MultipartFile file);

    default List<String> importData(MultipartFile file) {
        List<String> errores = validate(file);
        if (errores.isEmpty()) {
            saveAll(file);
        }
        return errores;
    }
}