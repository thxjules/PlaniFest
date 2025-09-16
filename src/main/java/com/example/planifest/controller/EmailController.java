package com.example.planifest.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.planifest.service.EmailServiceUser;

@RestController
public class EmailController {

    @Autowired
    private EmailServiceUser emailSend;

    @GetMapping("/enviar-masivo")
    public String enviarMasivo() {
        emailSend.enviarCorreosMasivos(
            "prueba masiva",
            "Este es un correo enviado a todos los usuarios de la BD."
        );

        return "Correos enviados";
    }
}
