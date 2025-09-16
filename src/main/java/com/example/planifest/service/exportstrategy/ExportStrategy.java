package com.example.planifest.service.exportstrategy;

import java.util.List;

public interface ExportStrategy<T> {

    String exportToJson(List<T> data) throws Exception;

    byte[] exportToCsv(List<T> data) throws Exception;

    byte[] exportToExcel(List<T> data) throws Exception;
}



