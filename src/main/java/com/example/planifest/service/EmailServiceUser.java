package com.example.planifest.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.planifest.repository.ClientRepository;
import com.example.planifest.repository.UserRepository;


@Service
public class EmailServiceUser {

    @Autowired
    private ClientRepository cliente;

    @Autowired
     private UserRepository user;


    @Autowired
    private EmailService emailService;

   public void enviarCorreosMasivos(String asunto, String texto) {
    List<String> destinarios = user.findAll()
            .stream()
            .map(u -> u.getEmail())
            .toList();

    for (String email : destinarios) {
        try {
            emailService.enviarCorreoIndividual(email, asunto, texto);
            Thread.sleep(10000); 
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("Error en la pausa entre correos: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Error enviando a " + email + ": " + e.getMessage());
        }
    }
}
public void enviarCorreoACliente(String email, String asunto, String texto) {
    emailService.enviarCorreoIndividual(email, asunto, texto);
}

}