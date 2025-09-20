package com.example.planifest.service;
import java.io.ByteArrayOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.xhtmlrenderer.pdf.ITextRenderer;

import com.lowagie.text.DocumentException;

@Service
public class ReportService {

    private final SpringTemplateEngine templateEngine;

    public ReportService(SpringTemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }
    public String encodeImageToBase64(String path) throws Exception {
    byte[] imageBytes = Files.readAllBytes(Paths.get(path));
    return Base64.getEncoder().encodeToString(imageBytes);
}
    public byte[] generatePdf(String templateName, Map<String, Object> variables) throws DocumentException {
        Context context = new Context();
        context.setVariables(variables);

        String htmlContent = templateEngine.process(templateName, context);

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            ITextRenderer renderer = new ITextRenderer();
            renderer.setDocumentFromString(htmlContent);
            renderer.layout();
            renderer.createPDF(outputStream);
            return outputStream.toByteArray();
        } catch (Exception e) {
            throw new DocumentException("Error al generar PDF: " + e.getMessage());
        }
    }
}