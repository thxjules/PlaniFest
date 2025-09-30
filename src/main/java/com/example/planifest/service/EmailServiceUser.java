package com.example.planifest.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.planifest.repository.ClientRepository;
import com.example.planifest.repository.UserRepository;

import java.util.List;

@Service
public class EmailServiceUser {

    @Autowired
    private ClientRepository clienteRepo;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private EmailService emailService;

    public void enviarCorreosMasivos(String asunto, String texto) {
        List<String> destinatarios = userRepo.findAll()
                .stream()
                .map(u -> u.getEmail())
                .filter(email -> email != null && !email.isBlank())
                .toList();

        for (String email : destinatarios) {
            try {
                emailService.enviarCorreoIndividual(email, asunto, texto);
                Thread.sleep(10000); // pausa entre correos
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.println("Error en la pausa entre correos: " + e.getMessage());
            } catch (Exception e) {
                System.out.println("Error enviando a " + email + ": " + e.getMessage());
            }
        }
    }

    public void enviarCorreoACliente(String email, String asunto, String texto) {
        if (email != null && !email.isBlank()) {
            emailService.enviarCorreoIndividual(email, asunto, texto);
        } else {
            System.out.println("⚠️ El cliente no tiene correo registrado.");
        }
    }
}
