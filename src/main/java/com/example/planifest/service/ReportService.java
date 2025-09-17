package com.example.planifest.service;

import java.io.ByteArrayOutputStream;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.lowagie.text.Document;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;

@Service
public class ReportService {

   public byte[] generatePdf(String titulo, Map<String, Object> data) {
    try {
        System.out.println("📥 Datos recibidos en generatePdf: " + data);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Document document = new Document();
        PdfWriter.getInstance(document, baos);

        document.open();
        document.add(new Paragraph("✅ PDF generado correctamente"));
        document.close();

        System.out.println("✅ PDF generado y cerrado correctamente");

        return baos.toByteArray();
    } catch (Exception e) {
        e.printStackTrace(); // <-- Aquí veremos el error real en consola
        throw new RuntimeException("Error generando PDF", e);
    }
}

}   
