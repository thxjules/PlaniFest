package com.example.planifest.service;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
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

    public String encodeImageToBase64FromClasspath(String classpathLocation) throws Exception {
    try (InputStream is = getClass().getClassLoader().getResourceAsStream(classpathLocation)) {

        if (is == null) {
            throw new IllegalArgumentException("No se encontró la imagen en el classpath: " + classpathLocation);
        }

        byte[] imageBytes = is.readAllBytes();
        return Base64.getEncoder().encodeToString(imageBytes);
    }
}


    public byte[] generatePdf(String templateName, Map<String, Object> variables) throws DocumentException {
        Context context = new Context();
        context.setVariables(variables);

        String htmlContent = templateEngine.process(templateName, context);

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            ITextRenderer renderer = new ITextRenderer();
            renderer.getSharedContext().setBaseURL(getClass().getResource("/static/").toString());
            renderer.setDocumentFromString(htmlContent);
            renderer.layout();
            renderer.createPDF(outputStream);
            return outputStream.toByteArray();
                    } catch (IOException e) {
            throw new DocumentException("Error al cargar recursos: " + e.getMessage());
        } catch (Exception e) {
            throw new DocumentException("Error al generar PDF: " + e.getMessage());
        }
        }
    }