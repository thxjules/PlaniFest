package com.example.planifest.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Async  
    public void enviarCorreosMasivos(List<String> destinatarios, String asunto, String mensajeTexto) {
        for (String destinatario : destinatarios) {
            enviarCorreoIndividual(destinatario, asunto, mensajeTexto);
        }
    }

    public void enviarCorreoIndividual(String destinatario, String asunto, String texto) {
    try {
        SimpleMailMessage mensaje = new SimpleMailMessage();
        mensaje.setTo(destinatario); 
        mensaje.setSubject(asunto);
        mensaje.setText(texto);
        mensaje.setFrom("Planifest@gmail.com");

        mailSender.send(mensaje);
        System.out.println("Correo enviado a: " + destinatario);
    } catch (Exception e) {
        System.err.println("Error enviando correo a " + destinatario + ": " + e.getMessage());
    }
}
}
